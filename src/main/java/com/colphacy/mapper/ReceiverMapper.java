package com.colphacy.mapper;

import com.colphacy.dto.receiver.ReceiverDTO;
import com.colphacy.model.Receiver;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReceiverMapper {
    ReceiverDTO receiverToReceiverDTO(Receiver receiver);
}