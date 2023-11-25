package com.colphacy.service.impl;

import com.colphacy.dao.OrderDAO;
import com.colphacy.dto.order.*;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.dto.product.ProductOrderSuitableDTO;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.OrderMapper;
import com.colphacy.model.*;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.repository.*;
import com.colphacy.service.BranchService;
import com.colphacy.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private BranchService branchService;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReceiverRepository receiverRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ProductUnitRepository productUnitRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private OrderItemRepository orderDetailRepository;
    @Autowired
    private OrderDAO orderDAO;

    @Override
    public OrderDTO createOrder(OrderCreateDTO orderCreateDTO, Customer customer) {
        Optional<Receiver> receiverOptional = receiverRepository.findByIdAndCustomerId(orderCreateDTO.getReceiverId(), customer.getId());

        if (receiverOptional.isEmpty()) {
            throw new RecordNotFoundException("Thông tin người nhận không đúng");
        }

        Receiver receiver = receiverOptional.get();

        Order order = new Order();
        order.setReceiver(receiver);
        order.setCustomer(customer);
        List<ProductOrderSuitableDTO> productOrderSuitables = orderDAO.findSuitableProduct(orderCreateDTO.getOrderItemCreateDTOs(), receiver.getAddress().getLatitude(), receiver.getAddress().getLongitude());

        if (productOrderSuitables.isEmpty()) {
            throw new RecordNotFoundException("Sản phẩm đã hết hàng");
        }

        List<OrderItem> items = productOrderSuitables.stream().map(item -> {
            Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> new RecordNotFoundException("Sản phẩm không tồn tại"));
            Unit unit = unitRepository.findById(item.getUnitId()).orElseThrow(() -> new RecordNotFoundException("Đơn vị không tồn tại"));
            ProductUnit productUnit = productUnitRepository.findByProductIdAndUnitId(product.getId(), unit.getId());

            if (productUnit == null) {
                throw new RecordNotFoundException("Sản phẩm không có đơn vị này");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setUnit(unit);
            orderItem.setPrice(item.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setRatio(productUnit.getRatio());
            // update later
            orderItem.setExpirationDate(item.getExpirationDate());

            return orderItem;
        }).toList();

        order.setOrderItems(items);
        Long branchId = productOrderSuitables.get(0).getBranchId();
        Branch branch = branchRepository.findById(branchId).get();
        order.setBranch(branch);
        Order savedOrder = orderRepository.save(order);

        // remove from cart

//        List<Long> productIds = items.stream()
//                .map(item -> item.getProduct().getId())
//                .toList();
//
//        List<Long> unitIds = items.stream()
//                .map(item -> item.getUnit().getId())
//                .toList();
//        // TODO (fix delete logic)
//        cartItemRepository.deleteByProductIdsAndUnitIdsAndCustomerId(productIds, unitIds, customer.getId());



        return orderMapper.orderToOrderDTO(savedOrder);
    }

    @Override
    public PageResponse<OrderListViewDTO> getPaginatedOrders(OrderSearchCriteria criteria) {

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
        }
        if (criteria.getBranchId() != null) {
            branchService.findBranchById(criteria.getBranchId());
        }
        // Validate maxPrice must be bigger or greater than minPrice
        if (criteria.getStartDate() != null && criteria.getEndDate() != null && criteria.getStartDate().isAfter(criteria.getEndDate())) {
            throw InvalidFieldsException.fromFieldError("endDate", "Ngày bắt đầu không thể lớn hơn ngày kết thúc");
        }

        List<OrderListViewDTO> list = orderDAO.getPaginatedOrders(criteria);

//        Long totalItems = orderDAO.getTotalOrders(criteria);
        Long totalItems = 10L;

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
    public void updateOrder(OrderUpdateDTO orderDTO) {
        Order order = findOrderById(orderDTO.getId());

        if (orderDTO.getToStatus() == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.PENDING) {
            throw InvalidFieldsException.fromFieldError("toStatus", "Không thể hủy đơn hàng ở trạng thái này");
        }
        LocalDateTime now = LocalDateTime.now();
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
        orderRepository.save(order);
    }
}
