package com.example.whoami.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageDTO {

    private String message;
    private LocalDateTime sendTime;
    private boolean wrapper;
    private String color;
}
