package com.colphacy.service;

import com.colphacy.dto.receiver.ReceiverDTO;
import com.colphacy.model.Customer;

import java.util.List;

public interface ReceiverService {
    ReceiverDTO create(ReceiverDTO receiverDTO, Customer customer);

    List<ReceiverDTO> getReceiversByCustomerId(Long customerId);

    ReceiverDTO getReceiver(Long receiverId, Long customerId);

    ReceiverDTO update(ReceiverDTO receiverDTO, Long customerId);

    void delete(Long receiverId, Long customerId);
}
