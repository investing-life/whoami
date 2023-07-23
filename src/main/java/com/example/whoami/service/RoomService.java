package com.example.whoami.service;

import com.example.whoami.dto.RoomInfoDTO;

public interface RoomService {
    public boolean roomMemberExist(String roomLink);

    public RoomInfoDTO getRoomByLink(String roomLink);

    public void createRoomMember(String roomLink, int indexNumber, String nickname, String roomColor);

    public void updateRoomByLink(String roomLink, String title);
}
