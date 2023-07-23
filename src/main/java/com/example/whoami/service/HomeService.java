package com.example.whoami.service;

import com.example.whoami.dto.RoomInfoDTO;

import java.util.List;

public interface HomeService {
    public List<RoomInfoDTO> getRoomByMemberId(int memberID);

    public boolean checkDuplicateRoomName(String roomName);

    public String createRoom(String name, String nickname, String roomColor);
}
