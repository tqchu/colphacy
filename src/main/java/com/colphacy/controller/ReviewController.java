package com.colphacy.controller;

import com.colphacy.dto.review.ReviewCustomerCreateDTO;
import com.colphacy.model.Customer;
import com.colphacy.service.CustomerService;
import com.colphacy.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ReviewService reviewService;

    /**
     * Customer send a review for product after order successfully
     * @param reviewCustomerCreateDTO The review's information to create
     * @throws com.colphacy.exception.RecordNotFoundException if the product could not be found
     * @throws com.colphacy.exception.InvalidFieldsException if the customer can not review the product
     */

    @PostMapping("")
    public void sendReview(@RequestBody @Valid ReviewCustomerCreateDTO reviewCustomerCreateDTO, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        reviewService.create(reviewCustomerCreateDTO, customer);
    }
}
