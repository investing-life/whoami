package com.example.whoami.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NoticeDTO {
    private int indexNumber;
    private int sequenceNumber;
    private String title;
    private String content;
    private LocalDateTime regDate;
}
