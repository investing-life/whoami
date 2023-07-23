package com.example.whoami.service;

import jakarta.transaction.Transactional;

public interface RoomMemberService {
    public void updateRoomMember(String roomLink, int indexNumber, String nickname, String roomColor);

    public String findNameByRoomLinkAndId(String roomLink, int roomMemberId);

    public void readMessage(String roomLink, int indexNumber);

    public void deleteRoomMember(String roomLink, int indexNumber);

    public void setTipPopup(String roomLink, int indexNumber, boolean value);
}
