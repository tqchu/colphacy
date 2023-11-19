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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public OrderDTO createOrder(OrderCreateDTO orderCreateDTO, Customer customer) {
        Optional<Receiver> receiverOptional = receiverRepository.findByCustomerId(customer.getId());

        if (receiverOptional.isEmpty()) {
            throw new RecordNotFoundException("Thông tin người nhận không đúng");
        }

        Receiver receiver = receiverOptional.get();

        Order order = new Order();
        order.setReceiver(receiver);
        order.setCustomer(customer);

        double totalPrice = 0;

        Set<OrderItem> items = orderCreateDTO.getOrderItemCreateDTOs().stream().map(orderItem -> {
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
            item.setBaseQuantity(orderItem.getQuantity() * productUnit.getRatio());

            return item;
        }).collect(Collectors.toSet());
        totalPrice = items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        order.setOrderItems(items);
        order.setTotalPrice(totalPrice);
        items.forEach(item -> item.setOrder(order));

        Order savedOrder = orderRepository.save(order);

        // TODO("remove from cart")

        return orderMapper.orderToOrderDTO(savedOrder);
    }
}
