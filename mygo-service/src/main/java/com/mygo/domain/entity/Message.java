package com.mygo.domain.entity;

import com.mygo.domain.enumeration.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Message {

    String fromId;

    String toId;

    MessageType messageType;

    String message;

    LocalDateTime time;

}
