package com.colphacy.mapper;

import com.colphacy.dto.CustomerDetailDTO;
import com.colphacy.dto.customer.CustomerSimpleDTO;
import com.colphacy.model.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDetailDTO customerToCustomerDetailDTO(Customer customer);

    CustomerSimpleDTO customerToCustomerSimpleDTO(Customer customer);
}
