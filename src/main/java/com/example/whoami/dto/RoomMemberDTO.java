package com.example.whoami.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomMemberDTO {
    private int roomId;
    private int memberId;
    private String memberName;
    private int roomMemberId;
    private int questionNum;
}
