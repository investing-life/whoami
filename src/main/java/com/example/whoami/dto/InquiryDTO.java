package com.example.whoami.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InquiryDTO {
    private String message;
    private String messageType;
    private boolean wrapper;
    private LocalDateTime sendTime;
}
