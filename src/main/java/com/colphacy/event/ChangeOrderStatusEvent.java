package com.colphacy.event;

import com.colphacy.model.Customer;
import com.colphacy.model.Order;
import com.colphacy.model.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class ChangeOrderStatusEvent extends ApplicationEvent {
    private Customer customer;
    private Long orderId;
    OrderStatus orderStatus;

    public ChangeOrderStatusEvent(Customer customer, Long orderId, OrderStatus orderStatus) {
        super(customer);
        this.customer = customer;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
    }
}
