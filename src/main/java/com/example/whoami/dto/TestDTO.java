package com.example.whoami.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestDTO {
    private String roomLink;
    // 방 내부에서 쓰는 room_member_id가 아니고 고유의 member_id이다 (idGenerator)
    private int indexNumber;
    private int toId;
    private float openness;
    private float conscientiousness;
    private float extraversion;
    private float agreeableness;
    private float neuroticism;
}
