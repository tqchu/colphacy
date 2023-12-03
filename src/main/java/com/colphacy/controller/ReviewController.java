package com.colphacy.controller;

import com.colphacy.dto.review.ReviewCustomerCreateDTO;
import com.colphacy.dto.review.ReviewCustomerListDTO;
import com.colphacy.model.Customer;
import com.colphacy.payload.response.PageResponse;
import com.colphacy.service.CustomerService;
import com.colphacy.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ReviewService reviewService;

    private final Integer defaultPageSize;

    @Autowired
    public ReviewController(Integer defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    /**
     * Customer send a review for product after order successfully
     *
     * @param reviewCustomerCreateDTO The review's information to create
     * @throws com.colphacy.exception.RecordNotFoundException if the product could not be found
     * @throws com.colphacy.exception.InvalidFieldsException  if the customer can not review the product
     */

    @PostMapping("")
    public void sendReview(@RequestBody @Valid ReviewCustomerCreateDTO reviewCustomerCreateDTO, Principal principal) {
        Customer customer = customerService.getCurrentlyLoggedInCustomer(principal);
        reviewService.create(reviewCustomerCreateDTO, customer);
    }

    @GetMapping("/product/{productId}")
    public PageResponse<ReviewCustomerListDTO> getAllReviewsForProduct(@PathVariable Long productId,
                                                                       @RequestParam(required = false, defaultValue = "0")
                                                                       @Min(value = 0, message = "Số bắt đầu phải là số không âm") int offset,
                                                                       @RequestParam(required = false) @Min(value = 1, message = "Số lượng giới hạn phải lớn hơn 0") Integer limit) {
        if (limit == null) {
            limit = defaultPageSize;
        }
        return reviewService.getAllReviewsForProduct(productId, offset, limit);
    }
}
