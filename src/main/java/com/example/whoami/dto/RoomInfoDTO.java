package com.example.whoami.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RoomInfoDTO {
    private String roomName;
    private LocalDate createDate;
    private String myName;
    private Map<Integer, String> memberList;
    private String roomLink;
    private boolean newMessage;
    private String roomColor;
    private boolean tipPopup;
}