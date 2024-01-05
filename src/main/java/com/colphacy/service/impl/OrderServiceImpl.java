package com.colphacy.service.impl;

import com.colphacy.dao.CartDAO;
import com.colphacy.dao.OrderDAO;
import com.colphacy.dto.cart.CartItemTuple;
import com.colphacy.dto.order.*;
import com.colphacy.dto.product.ProductOrderItem;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.NotificationMapper;
import com.colphacy.mapper.OrderItemMapper;
import com.colphacy.mapper.OrderMapper;
import com.colphacy.model.*;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.repository.*;
import com.colphacy.service.*;
import com.colphacy.util.HashingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private BranchService branchService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ReceiverRepository receiverRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ProductUnitRepository productUnitRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private CartDAO cartDAO;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ReceiverService receiverService;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationMapper notificationMapper;
    @Value("${order-notification-icon-url}")
    private String orderIconUrl;

    @Value("${order-management-admin-web-url}")
    private String orderManagementAdminWebUrl;
    @Value("${vnpay.version}")
    private String vnpayVersion;
    @Value("${vnpay.tmncode}")
    private String vnpayTmnCode;
    @Value("${vnpay.secret-key}")
    private String vnpaySecretKey;
    private final String vnpayPayCommand = "pay";
    private final String vnpayReturnUrl = "https://colphacy.tech/api/orders/payments/return";
    private final String vnpayPaymentUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    @Transactional
    @Override
    public OrderDTO purchase(OrderPurchaseDTO orderPurchaseDTO, Customer customer, HttpServletRequest request) throws UnsupportedEncodingException {
        Optional<Receiver> receiverOptional = receiverRepository.findByIdAndCustomerId(orderPurchaseDTO.getReceiverId(), customer.getId());

        if (receiverOptional.isEmpty()) {
            throw new RecordNotFoundException("Thông tin người nhận không đúng");
        }
        Receiver receiver = receiverOptional.get();

        orderPurchaseDTO.getItems().forEach(item ->
                {
                    if (!productRepository.existsById(item.getProductId())) {
                        throw new RecordNotFoundException("Sản phẩm không tồn tại");
                    }
                    if (!unitRepository.existsById(item.getUnitId())) {
                        throw new RecordNotFoundException("Đơn vị tính không tồn tại");
                    }
                    if (!productUnitRepository.existsByProductIdAndUnitId(item.getProductId(), item.getUnitId())) {
                        throw new RecordNotFoundException("Sản phẩm không có đơn vị tính này");
                    }
                }
        );

        List<ProductOrderItem> availableProducts = orderDAO.findAvailableProducts(orderPurchaseDTO.getItems(), receiver.getAddress().getLatitude(), receiver.getAddress().getLongitude());

        if (availableProducts.isEmpty()) {
            throw new RecordNotFoundException("Sản phẩm đã hết hàng");
        }

        Order order = new Order();
        order.setReceiver(receiver);
        order.setCustomer(customer);

        List<OrderItem> items = availableProducts.stream().map(orderItemMapper::productOrderItemToOrderItem).toList();
        order.setOrderItems(items);
        Long branchId = availableProducts.get(0).getBranchId();
        Branch branch = new Branch();
        branch.setId(branchId);
        order.setBranch(branch);
        order.setNote(orderPurchaseDTO.getNote());
        order.setPaymentMethod(orderPurchaseDTO.getPaymentMethod());
        if (orderPurchaseDTO.getPaymentMethod() == PaymentMethod.ONLINE) {
            order.setStatus(OrderStatus.TO_PAY);
        }

        Order savedOrder = orderRepository.save(order);

        // remove bought items from cart
        List<CartItemTuple> cartItemTuples = orderPurchaseDTO.getItems().stream()
                .map(item -> {
                    CartItemTuple tuple = new CartItemTuple();
                    tuple.setCustomerId(customer.getId());
                    tuple.setProductId(item.getProductId());
                    tuple.setUnitId(item.getUnitId());
                    return tuple;
                }).toList();
        cartDAO.deleteByCustomerIdAndProductIdAndUnitId(cartItemTuples);

        // Send the notification immediately if method is ON_DELIVERY
        if (orderPurchaseDTO.getPaymentMethod() != PaymentMethod.ONLINE) {
            sendPushNotifications(customer, order);
        }
        OrderDTO result = orderMapper.orderToOrderDTO(savedOrder);
        if (result.getPaymentMethod() == PaymentMethod.ONLINE) {
            result.setPaymentLink(getPaymentUrl(result, request));
        }
        return result;
    }

    private void sendPushNotifications(Customer customer, Order order) {
        // asynchronous add notifications for employee
        // Query all employee
        List<Employee> employees = employeeRepository.findEmployeeByOfABranch(order.getBranch().getId());

        // Create a list to hold all the CompletableFuture objects
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Employee employee : employees) {
            // Run each notification creation and save operation in a separate thread
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Notification notification = new Notification();
                notification.setEmployee(employee);
                switch (order.getStatus()) {
                    case PENDING:
                        notification.setDescription("Khách hàng " + customer.getFullName() + " đã đặt đơn hàng " + order.getId() + ", hãy xem ngay");
                        notification.setTitle("Có đơn hàng mới!");
                        break;
                    case DELIVERED:
                        notification.setDescription("Khách hàng " + customer.getFullName() + " đã nhận đơn hàng " + order.getId() + ", hãy xem ngay");
                        notification.setTitle("Khách hàng đã nhận đơn hàng #" + order.getId() + "!");
                        break;
                    case CANCELLED:
                        notification.setDescription("Khách hàng " + customer.getFullName() + " đã hủy đơn hàng " + order.getId());
                        notification.setTitle("Khách hàng đã hủy đơn hàng #" + order.getId() + "!");
                        break;
                }

                notification.setImage(orderIconUrl);
                // TODO: switch case for it
                notification.setUrl(orderManagementAdminWebUrl);
                notificationRepository.save(notification);
                notificationService.publishNotification(notificationMapper.notificationToNotificationDTO(notification));
            });

            // Add the CompletableFuture to the list
            futures.add(future);
        }

        // Wait for all the CompletableFuture objects to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    @Transactional
    public OrderDTO createOrder(OrderCreateDTO orderDTO, Employee employee) {
        // validate branch or select branch
        Branch branch = employee.getBranch();

        Order order = new Order();
        if (branch != null) {
            order.setBranch(branch);
        } else {
            branch = branchService.findBranchById(orderDTO.getBranchId());
            order.setBranch(branch);
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        if (orderDTO.getOrderTime() != null) {
//            if (orderDTO.getOrderTime().isAfter(now)) {
////                throw InvalidFieldsException.fromFieldError("orderTime", "Thời gian mua hàng không hợp lệ");
//            } else order.setOrderTime(orderDTO.getOrderTime());
            order.setOrderTime(orderDTO.getOrderTime());
        } else order.setOrderTime(now);
        order.setConfirmTime(now);
        order.setPaymentMethod(PaymentMethod.ON_DELIVERY);
        order.setShipTime(now);
        order.setDeliverTime(now);
        List<ProductOrderItem> availableProducts = orderDAO.findAvailableProductsForABranch(orderDTO.getItems(), branch.getId());

        if (availableProducts.isEmpty()) {
            throw new RecordNotFoundException("Sản phẩm đã hết hàng");
        }

        List<OrderItem> items = availableProducts.stream().map(orderItemMapper::productOrderItemToOrderItem).toList();
        order.setOrderItems(items);

        Customer customer = customerService.findById(orderDTO.getCustomerId());
        order.setCustomer(customer);

        order.setStatus(OrderStatus.DELIVERED);
        // Find receiver by customerId and branchId
        Receiver receiver = receiverService.findByCustomerIdAndBranchId(customer.getId(), branch.getId());
        if (receiver == null) {
            receiver = new Receiver();
            receiver.setName(customer.getFullName());
            receiver.setPhone(customer.getPhone());
            receiver.setAddress(branch.getAddress());
            receiver.setBranchId(branch.getId());
            receiver.setCustomer(customer);
            receiver.setIsPrimary(false);
            receiverRepository.save(receiver);
        }
        order.setReceiver(receiver);
        order.setNote(orderDTO.getNote());
        orderRepository.save(order);
        return orderMapper.orderToOrderDTO(order);
    }

    @Override
    public PageResponse<OrderListViewDTO> getPaginatedOrders(OrderSearchCriteria criteria) {
        criteria.setCustomerId(null);
        if (criteria.getStatus() == null) {
            criteria.setStatus(OrderStatus.PENDING);
        }
        // Handle sort field
        if (criteria.getSortBy() != null && criteria.getSortBy().name().equalsIgnoreCase("time")) {
            if (criteria.getStatus() == null || criteria.getStatus() == OrderStatus.PENDING) {
                criteria.setSortBy(OrderListSortField.ORDER_TIME);
            } else if (criteria.getStatus() == OrderStatus.CONFIRMED) {
                criteria.setSortBy(OrderListSortField.CONFIRM_TIME);
            } else if (criteria.getStatus() == OrderStatus.SHIPPING) {
                criteria.setSortBy(OrderListSortField.SHIP_TIME);
            } else if (criteria.getStatus() == OrderStatus.DELIVERED) {
                criteria.setSortBy(OrderListSortField.DELIVER_TIME);
            } else if (criteria.getStatus() == OrderStatus.CANCELLED) {
                criteria.setSortBy(OrderListSortField.CANCEL_TIME);
            }
            // TODO: add for resolved type
        }
        if (criteria.getBranchId() != null) {
            branchService.findBranchDetailDTOById(criteria.getBranchId());
        }
        // Validate maxPrice must be bigger or greater than minPrice
        if (criteria.getStartDate() != null && criteria.getEndDate() != null && criteria.getStartDate().isAfter(criteria.getEndDate())) {
            throw InvalidFieldsException.fromFieldError("endDate", "Ngày bắt đầu không thể lớn hơn ngày kết thúc");
        }

        if (criteria.getEndDate() != null) {
            criteria.setEndDate(criteria.getEndDate().plusDays(1));
        }
        List<OrderListViewDTO> list = orderDAO.getPaginatedOrders(criteria);

        Long totalItems = orderDAO.getTotalOrders(criteria);

        PageResponse<OrderListViewDTO> page = new PageResponse<>();
        page.setItems(list);
        page.setNumPages((int) ((totalItems - 1) / criteria.getLimit()) + 1);
        page.setLimit(criteria.getLimit());
        page.setTotalItems(Math.toIntExact(totalItems));
        page.setOffset(criteria.getOffset());
        return page;
    }

    public Order findOrderById(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            throw new RecordNotFoundException("Không tìm thấy đơn hàng có id = " + id);
        }

        return optionalOrder.get();
    }

    @Override
    public String getPaymentUrl(Long id, HttpServletRequest request) throws UnsupportedEncodingException {
        OrderDTO order = findOrderDTOById(id);
        return getPaymentUrl(order, request);
    }

    @Override
    public Order requestReturnOrder(Long id, Long customerId) {
        Order order = findOrderByIdAndCustomerId(id, customerId);
        if (order.getStatus() == OrderStatus.SHIPPING || order.getStatus() == OrderStatus.DELIVERED) {
            order.setRequestReturnTime(ZonedDateTime.now(ZoneOffset.UTC));
            order.setStatus(OrderStatus.RETURNED);
            order.setResolveType(ResolveType.PENDING);
            order.setAdminConfirmDeliver(false);
            return orderRepository.save(order);
        } else {
            throw InvalidFieldsException.fromFieldError("error", "Không thể yêu cầu trả hàng đơn hàng ở trạng thái này");
        }
    }

    @Override
    public Order resolveReturnRequests(Long id, boolean accepted) {
        Order order = findOrderById(id);

        if (order.getStatus() == OrderStatus.RETURNED) {
            if (!accepted) {
                order.setResolveType(ResolveType.REFUSED);
            } else {
                if (order.getPaymentMethod() == PaymentMethod.ON_DELIVERY) {
                    order.setResolveType(ResolveType.RETURN);
                } else
                    order.setResolveType(ResolveType.REFUND);
            }
            order.setResolveTime(ZonedDateTime.now(ZoneOffset.UTC));
        } else {
            throw InvalidFieldsException.fromFieldError("error", "Yêu cầu không hợp lệ");
        }
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrder(OrderUpdateDTO orderDTO) {
        Order order = findOrderById(orderDTO.getId());

        if (orderDTO.getToStatus() == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.PENDING) {
            throw InvalidFieldsException.fromFieldError("toStatus", "Không thể hủy đơn hàng ở trạng thái này");
        }
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        if (order.getStatus() == OrderStatus.PENDING) {
            if (orderDTO.getToStatus() == OrderStatus.CANCELLED) {
                order.setCancelTime(now);
                order.setStatus(OrderStatus.CANCELLED);
                order.setCancelBy(CancelType.EMPLOYEE);
            } else {
                order.setStatus(OrderStatus.CONFIRMED);
                order.setConfirmTime(now);
            }
        } else if (order.getStatus() == OrderStatus.CONFIRMED) {
            order.setShipTime(now);
            order.setStatus(OrderStatus.SHIPPING);
        } else if (order.getStatus() == OrderStatus.SHIPPING) {
            order.setAdminConfirmDeliver(true);
            order.setAdminConfirmDeliverTime(now);
        }
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(Long id, Customer customer) {
        Order order = findOrderByIdAndCustomerId(id, customer.getId());

        if (order.getStatus() != OrderStatus.PENDING) {
            throw InvalidFieldsException.fromFieldError("error", "Không thể hủy đơn hàng ở trạng thái này");
        }
        order.setCancelTime(ZonedDateTime.now(ZoneOffset.UTC));
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelBy(CancelType.CUSTOMER);
        sendPushNotifications(customer, order);
        return orderRepository.save(order);
    }

    @Override
    public OrderDTO findOrderDTOById(Long id) {
        Order order = findOrderById(id);

        return orderMapper.orderToOrderDTO(order);
    }


    public PageResponse<OrderListViewCustomerDTO> getPaginatedOrdersCustomer(OrderSearchCriteria criteria) {
        // Handle sort field
        if (criteria.getSortBy() != null && criteria.getSortBy().name().equalsIgnoreCase("time")) {
            if (criteria.getStatus() == null || criteria.getStatus() == OrderStatus.PENDING) {
                criteria.setSortBy(OrderListSortField.ORDER_TIME);
            } else if (criteria.getStatus() == OrderStatus.CONFIRMED) {
                criteria.setSortBy(OrderListSortField.CONFIRM_TIME);
            } else if (criteria.getStatus() == OrderStatus.SHIPPING) {
                criteria.setSortBy(OrderListSortField.SHIP_TIME);
            } else if (criteria.getStatus() == OrderStatus.DELIVERED) {
                criteria.setSortBy(OrderListSortField.DELIVER_TIME);
            } else if (criteria.getStatus() == OrderStatus.CANCELLED) {
                criteria.setSortBy(OrderListSortField.CANCEL_TIME);
            } else if (criteria.getStatus() == OrderStatus.RETURNED) {
                criteria.setSortBy(OrderListSortField.REQUEST_RETURN_TIME);
            }
        }
        if (criteria.getBranchId() != null) {
            branchService.findBranchDetailDTOById(criteria.getBranchId());
        }
        // Validate maxPrice must be bigger or greater than minPrice
        if (criteria.getStartDate() != null && criteria.getEndDate() != null && criteria.getStartDate().isAfter(criteria.getEndDate())) {
            throw InvalidFieldsException.fromFieldError("endDate", "Ngày bắt đầu không thể lớn hơn ngày kết thúc");
        }

        if (criteria.getEndDate() != null) {
            criteria.setEndDate(criteria.getEndDate().plusDays(1));
        }
        List<OrderListViewCustomerDTO> list = orderDAO.getPaginatedOrdersForCustomer(criteria);

        Long totalItems = orderDAO.getTotalOrdersForCustomer(criteria);


        PageResponse<OrderListViewCustomerDTO> page = new PageResponse<>();
        page.setItems(list);
        page.setNumPages((int) ((totalItems - 1) / criteria.getLimit()) + 1);
        page.setLimit(criteria.getLimit());
        page.setTotalItems(Math.toIntExact(totalItems));
        page.setOffset(criteria.getOffset());
        return page;
    }

    @Override
    public OrderDTO findOrderDTOByIdAndCustomerId(Long orderId, Long customerId) {
        Order order = findOrderByIdAndCustomerId(orderId, customerId);

        return orderMapper.orderToOrderDTO(order);
    }

    @Override
    public Order completeOrder(Long id, Customer customer) {
        Order order = findOrderByIdAndCustomerId(id, customer.getId());
        if (order.getStatus() == OrderStatus.SHIPPING) {
            order.setDeliverTime(ZonedDateTime.now(ZoneOffset.UTC));
            order.setStatus(OrderStatus.DELIVERED);
            sendPushNotifications(customer, order);
            return orderRepository.save(order);
        } else {
            throw InvalidFieldsException.fromFieldError("error", "Không thể hoàn thành đơn hàng ở trạng thái này");
        }
    }

    @Override
    public Integer handlePaymentReturn(HttpServletRequest request) {
        Map fields = new HashMap();
        for (Enumeration params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = (String) params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        // Check checksum
        String signValue = HashingUtils.hashAllFields(fields, vnpaySecretKey);
        // TODO: fix checking
//        if (signValue.equals(vnp_SecureHash)) {

        String txnRef = request.getParameter("vnp_TxnRef");
        Long orderId = Long.parseLong(txnRef);
        if (orderId <= 0) {
            return -1;
        }
        Order order = findOrderById(orderId);

        double totalPrice = order.getOrderItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        double vnpAmount = Double.parseDouble(request.getParameter("vnp_Amount"));
        if (vnpAmount <= 0) {
            return -1;
        }

        boolean checkAmount = (totalPrice * 100 == vnpAmount);
        if (checkAmount) {
            order.setPaid(true);
            order.setPayTime(ZonedDateTime.now(ZoneOffset.UTC));
            order.setStatus(OrderStatus.PENDING);
            orderRepository.save(order);
            return 1;
        } else return 0;


//        return -1;
    }

    private String getPaymentUrl(OrderDTO order, HttpServletRequest request) throws UnsupportedEncodingException {
        String vnp_OrderInfo = "Pay for order " + order.getId();
        String orderType = "other";

        long totalPrice = (long) order.getOrderItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        long amount = totalPrice * 100;
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnpayVersion);
        vnp_Params.put("vnp_Command", vnpayPayCommand);
        vnp_Params.put("vnp_TmnCode", vnpayTmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", String.valueOf(order.getId()));
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = request.getParameter("language");
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }

        vnp_Params.put("vnp_ReturnUrl", vnpayReturnUrl);
        vnp_Params.put("vnp_IpAddr", HashingUtils.getIpAddress(request));

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("+7"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(now);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        String vnp_ExpireDate = formatter.format(now.plusMinutes(15));
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        //Build data to hash and querystring
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        //...
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = HashingUtils.hmacSHA512(vnpaySecretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return vnpayPaymentUrl + "?" + queryUrl;
    }

    private Order findOrderByIdAndCustomerId(Long orderId, Long customerId) {
        Optional<Order> optionalOrder = orderRepository.findByIdAndCustomerId(orderId, customerId);
        if (optionalOrder.isEmpty()) {
            throw new RecordNotFoundException("Không tìm thấy đơn hàng có id = " + orderId);
        }

        return optionalOrder.get();
    }
}
