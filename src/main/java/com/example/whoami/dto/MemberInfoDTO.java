package com.example.whoami.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MemberInfoDTO {
    private String id;
    private String email;
    private String oauth;
    private LocalDate joinDate;
    private boolean admin;
    private float openness;
    private float conscientiousness;
    private float extraversion;
    private float agreeableness;
    private float neuroticism;
    private boolean testDone;
}
