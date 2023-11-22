package com.colphacy.service.impl;

import com.colphacy.dto.order.OrderCreateDTO;
import com.colphacy.dto.order.OrderDTO;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.OrderMapper;
import com.colphacy.model.*;
import com.colphacy.repository.*;
import com.colphacy.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
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

        List<OrderItem> items = orderCreateDTO.getOrderItemCreateDTOs().stream().map(orderItem -> {
            Product product = productRepository.findById(orderItem.getProductId()).orElseThrow(() -> new RecordNotFoundException("Sản phẩm không tồn tại"));
            Unit unit = unitRepository.findById(orderItem.getUnitId()).orElseThrow(() -> new RecordNotFoundException("Đơn vị không tồn tại"));
            ProductUnit productUnit = productUnitRepository.findByProductIdAndUnitId(product.getId(), unit.getId());

            if (productUnit == null) {
                throw new RecordNotFoundException("Sản phẩm không có đơn vị này");
            }

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setUnit(unit);
            item.setPrice(orderItem.getPrice());
            item.setQuantity(orderItem.getQuantity());
            item.setRatio(productUnit.getRatio());
            // update later
            item.setExpirationDate(LocalDate.now());

            return item;
        }).toList();

        order.setOrderItems(items);
        // fetch branch, updated later
        Branch branch = branchRepository.findAll().get(0);
        order.setBranch(branch);
        // TODO("finding branch to sell products is also important, just only one branch handle an order.
        //so if the customer buy the product online, we must find the suitable branch")
        Order savedOrder = orderRepository.save(order);

        List<Long> productIds = items.stream()
                .map(item -> item.getProduct().getId())
                .toList();

        List<Long> unitIds = items.stream()
                .map(item -> item.getUnit().getId())
                .toList();
        // TODO (fix delete logic)
        cartItemRepository.deleteByProductIdsAndUnitIdsAndCustomerId(productIds, unitIds, customer.getId());


        return orderMapper.orderToOrderDTO(savedOrder);
    }
}
