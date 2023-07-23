package com.example.whoami.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "room_test")
public class RoomTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "index_number")
    private long indexNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name="room_from_id", referencedColumnName="room_id"),
            @JoinColumn(name="from_id", referencedColumnName="room_member_id")
    })
    private RoomMember roomMemberFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name="room_to_id", referencedColumnName="room_id"),
            @JoinColumn(name="to_id", referencedColumnName="room_member_id")
    })
    private RoomMember roomMemberTo;

    private float openness;
    private float conscientiousness;
    private float extraversion;
    private float agreeableness;
    private float neuroticism;
}
