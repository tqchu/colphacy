package com.colphacy.service.impl;

import com.colphacy.dao.CartDAO;
import com.colphacy.dao.OrderDAO;
import com.colphacy.dto.cart.CartItemTuple;
import com.colphacy.dto.order.*;
import com.colphacy.dto.product.ProductOrderItem;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.OrderItemMapper;
import com.colphacy.mapper.OrderMapper;
import com.colphacy.model.*;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.repository.*;
import com.colphacy.service.BranchService;
import com.colphacy.service.CustomerService;
import com.colphacy.service.OrderService;
import com.colphacy.service.ReceiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @Value("${order-notification-icon-url}")
    private String orderIconUrl;

    @Value("${order-management-admin-web-url}")
    private String orderManagementAdminWebUrl;

    @Transactional
    @Override
    public OrderDTO purchase(OrderPurchaseDTO orderPurchaseDTO, Customer customer) {
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
                notification.setDescription("Khách hàng " + customer.getFullName() + " đã đặt đơn hàng " + order.getId() + ", hãy xem ngay");
                notification.setTitle("Có đơn hàng mới!");
                notification.setImage(orderIconUrl);
                notification.setUrl(orderManagementAdminWebUrl);
                notificationRepository.save(notification);
            });

            // Add the CompletableFuture to the list
            futures.add(future);
        }

        // Wait for all the CompletableFuture objects to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return orderMapper.orderToOrderDTO(savedOrder);
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

        ZonedDateTime now = ZonedDateTime.now();
        if (orderDTO.getOrderTime() != null) {
//            if (orderDTO.getOrderTime().isAfter(now)) {
////                throw InvalidFieldsException.fromFieldError("orderTime", "Thời gian mua hàng không hợp lệ");
//            } else order.setOrderTime(orderDTO.getOrderTime());
            order.setOrderTime(orderDTO.getOrderTime());
        } else order.setOrderTime(now);
        order.setConfirmTime(now);
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
            } else if (criteria.getStatus() == OrderStatus.COMPLETED) {
                criteria.setSortBy(OrderListSortField.COMPLETE_TIME);
            }
        }
        if (criteria.getBranchId() != null) {
            branchService.findBranchDetailDTOById(criteria.getBranchId());
        }
        // Validate maxPrice must be bigger or greater than minPrice
        if (criteria.getStartDate() != null && criteria.getEndDate() != null && criteria.getStartDate().isAfter(criteria.getEndDate())) {
            throw InvalidFieldsException.fromFieldError("endDate", "Ngày bắt đầu không thể lớn hơn ngày kết thúc");
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

    private Order findOrderById(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            throw new RecordNotFoundException("Không tìm thấy đơn hàng có id = " + id);
        }

        return optionalOrder.get();
    }

    @Override
    public Order updateOrder(OrderUpdateDTO orderDTO) {
        Order order = findOrderById(orderDTO.getId());

        if (orderDTO.getToStatus() == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.PENDING) {
            throw InvalidFieldsException.fromFieldError("toStatus", "Không thể hủy đơn hàng ở trạng thái này");
        }
        ZonedDateTime now = ZonedDateTime.now();
        if (order.getStatus() == OrderStatus.PENDING) {
            if (orderDTO.getToStatus() == OrderStatus.CANCELLED) {
                order.setCancelTime(now);
                order.setStatus(OrderStatus.CANCELLED);
            } else {
                order.setStatus(OrderStatus.CONFIRMED);
                order.setConfirmTime(now);
            }
        } else if (order.getStatus() == OrderStatus.CONFIRMED) {
            order.setShipTime(now);
            order.setStatus(OrderStatus.SHIPPING);
        } else if (order.getStatus() == OrderStatus.SHIPPING) {
            order.setDeliverTime(now);
            order.setStatus(OrderStatus.DELIVERED);
        }
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(Long id, Long customerId) {
        Order order = findOrderByIdAndCustomerId(id, customerId);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw InvalidFieldsException.fromFieldError("error", "Không thể hủy đơn hàng ở trạng thái này");
        }
        order.setCancelTime(ZonedDateTime.now());
        order.setStatus(OrderStatus.CANCELLED);
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
            } else if (criteria.getStatus() == OrderStatus.COMPLETED) {
                criteria.setSortBy(OrderListSortField.COMPLETE_TIME);
            }
        }
        if (criteria.getBranchId() != null) {
            branchService.findBranchDetailDTOById(criteria.getBranchId());
        }
        // Validate maxPrice must be bigger or greater than minPrice
        if (criteria.getStartDate() != null && criteria.getEndDate() != null && criteria.getStartDate().isAfter(criteria.getEndDate())) {
            throw InvalidFieldsException.fromFieldError("endDate", "Ngày bắt đầu không thể lớn hơn ngày kết thúc");
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
    public Order completeOrder(Long id, Long customerId) {
        Order order = findOrderByIdAndCustomerId(id, customerId);
        if (order.getStatus() == OrderStatus.SHIPPING || order.getStatus() == OrderStatus.DELIVERED) {
            order.setCompleteTime(ZonedDateTime.now());
            order.setStatus(OrderStatus.COMPLETED);
            return orderRepository.save(order);
        } else {
            throw InvalidFieldsException.fromFieldError("error", "Không thể hoàn thành đơn hàng ở trạng thái này");
        }
    }

    private Order findOrderByIdAndCustomerId(Long orderId, Long customerId) {
        Optional<Order> optionalOrder = orderRepository.findByIdAndCustomerId(orderId, customerId);
        if (optionalOrder.isEmpty()) {
            throw new RecordNotFoundException("Không tìm thấy đơn hàng có id = " + orderId);
        }

        return optionalOrder.get();
    }
}
