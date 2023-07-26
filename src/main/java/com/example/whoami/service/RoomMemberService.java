package com.example.whoami.service;

import com.example.whoami.dto.RoomMemberDTO;

public interface RoomMemberService {
    public void updateRoomMember(String roomLink, int indexNumber, String nickname, String roomColor);

    public String findNameByRoomLinkAndIdAndDeleted(String roomLink, int roomMemberId, boolean deleted);

    public int findMemberIdByRoomLinkAndIdAndDeleted(String roomLink, int roomMemberId, boolean deleted);

    public void readMessage(String roomLink, int indexNumber);

    public void deleteRoomMember(String roomLink, int indexNumber);

    public void setTipPopup(String roomLink, int indexNumber, boolean value);

    public boolean startTip(String roomLink, int indexNumber);

    public RoomMemberDTO findByRoomMemberId(String roomLink, int roomMemberId, boolean deleted);

    public RoomMemberDTO findByMemberID(String roomLink, int memberId, boolean deleted);
}
