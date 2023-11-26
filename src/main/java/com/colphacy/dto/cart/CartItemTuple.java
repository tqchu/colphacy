package com.colphacy.dto.cart;

import lombok.Data;

/**
 * This class represents a tuple of cart item identifiers.
 * It is used to group the identifiers (customerId, productId, unitId) together
 * into a single object for easier handling, particularly when performing batch operations
 * such as deleting multiple cart items at once.
 */
@Data
public class CartItemTuple {
    private Long customerId;
    private Long productId;
    private Long unitId;
}
