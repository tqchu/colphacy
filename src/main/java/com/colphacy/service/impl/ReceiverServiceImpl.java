package com.colphacy.service.impl;

import com.colphacy.dto.receiver.ReceiverDTO;
import com.colphacy.exception.InvalidFieldsException;
import com.colphacy.exception.RecordNotFoundException;
import com.colphacy.mapper.ReceiverMapper;
import com.colphacy.model.Customer;
import com.colphacy.model.Receiver;
import com.colphacy.repository.ReceiverRepository;
import com.colphacy.service.ReceiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class ReceiverServiceImpl implements ReceiverService {
    @Autowired
    private ReceiverRepository receiverRepository;

    @Autowired
    private ReceiverMapper receiverMapper;

    @Override
    public ReceiverDTO create(ReceiverDTO receiverDTO, Customer customer) {
        receiverDTO.setId(null);
        Receiver receiver = receiverMapper.receiverDTOToReceiver(receiverDTO);
        if (receiverDTO.getIsPrimary()) {
            receiverRepository.resetPrimaryReceiverByCustomerId(customer.getId());
        }
        receiver.setCustomer(customer);
        receiver = receiverRepository.save(receiver);
        return receiverMapper.receiverToReceiverDTO(receiver);
    }

    @Override
    public List<ReceiverDTO> getReceiversByCustomerId(Long customerId) {
        List<Receiver> receivers = receiverRepository.findByCustomerId(customerId);
        return receivers.stream().map(receiver -> receiverMapper.receiverToReceiverDTO(receiver)).toList();
    }

    @Override
    public ReceiverDTO getReceiver(Long receiverId, Long customerId) {
        Receiver receiver = findByReceiverIdAndCustomerId(receiverId, customerId);
        return receiverMapper.receiverToReceiverDTO(receiver);
    }

    @Override
    public ReceiverDTO update(ReceiverDTO receiverDTO, Long customerId) {
        if (receiverDTO.getId() == null) {
            throw InvalidFieldsException.fromFieldError("id", "Id là trường bắt buộc");
        }

        Receiver existingReceiver = findByReceiverIdAndCustomerId(receiverDTO.getId(), customerId);
        existingReceiver.setPhone(receiverDTO.getPhone());
        existingReceiver.setName(receiverDTO.getName());
        existingReceiver.setAddress(receiverDTO.getAddress());
        if (receiverDTO.getIsPrimary()) {
            receiverRepository.resetPrimaryReceiverByCustomerId(customerId);
        }
        existingReceiver.setIsPrimary(receiverDTO.getIsPrimary());
        Receiver receiverUpdated = receiverRepository.save(existingReceiver);

        return receiverMapper.receiverToReceiverDTO(receiverUpdated);
    }

    @Override
    public void delete(Long receiverId, Long customerId) {
        List<Receiver> receivers = receiverRepository.findByCustomerId(customerId);
        if (receivers.size() <= 1) {
            throw InvalidFieldsException.fromFieldError("id", "Tài khoản phải có ít nhất một thông tin người nhận");
        }

        Receiver receiver = findByReceiverIdAndCustomerId(receiverId, customerId);
        receiver.setDeletedAt(ZonedDateTime.now());
        receiverRepository.save(receiver);
    }

    @Override
    public Receiver findByCustomerIdAndBranchId(Long customerId, Long branchId) {
        return receiverRepository.findByCustomerIdAndBranchId(customerId, branchId);
    }

    private Receiver findByReceiverIdAndCustomerId(Long receiverId, Long customerId) {
        return receiverRepository.findByIdAndCustomerId(receiverId, customerId).orElseThrow(() -> new RecordNotFoundException("Người nhận không tồn tại"));
    }
}
