package com.colphacy.controller;

import com.colphacy.dto.receiver.ReceiverDTO;
import com.colphacy.model.Customer;
import com.colphacy.service.CustomerService;
import com.colphacy.service.ReceiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/receivers")
public class ReceiverController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private ReceiverService receiverService;

    @PostMapping
    public ReceiverDTO create(@RequestBody @Valid ReceiverDTO receiverDTO, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        return receiverService.create(receiverDTO, customer);
    }

    @GetMapping("/{id}")
    public ReceiverDTO getReceiver(@PathVariable Long id, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        return receiverService.getReceiver(id, customer.getId());
    }

    @GetMapping
    public List<ReceiverDTO> getReceiversByCustomer(Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        return receiverService.getReceiversByCustomerId(customer.getId());
    }

    @PutMapping()
    public ReceiverDTO update(@RequestBody @Valid ReceiverDTO receiverDTO, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        return receiverService.update(receiverDTO, customer.getId());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        receiverService.delete(id, customer.getId());
    }
}
