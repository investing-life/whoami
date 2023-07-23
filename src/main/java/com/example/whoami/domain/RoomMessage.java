package com.example.whoami.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "room_message")
public class RoomMessage {

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

    @Column(name = "message")
    private String message;

    @Column(name = "send_time", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime sendTime;

    @Column(name = "color")
    private String color;

    public int getRoomFromId() {
        return this.roomMemberFrom != null ? this.roomMemberFrom.getRoomId() : 0;
    }

    public void setRoomFromId(int id) {
        if (this.roomMemberFrom == null) {
            this.roomMemberFrom = new RoomMember();
        }
        this.roomMemberFrom.setRoomId(id);
    }

    public int getRoomToId() {
        return this.roomMemberTo != null ? this. roomMemberTo.getRoomId() : 0;
    }

    public void setRoomToId(int id) {
        if (this.roomMemberTo == null) {
            this.roomMemberTo = new RoomMember();
        }
        this.roomMemberTo.setRoomId(id);
    }

    public int getFromId() {
        return this.roomMemberFrom != null ? this.roomMemberFrom.getRoomMemberId() : 0;
    }

    public void setFromId(int id) {
        if (this.roomMemberFrom == null) {
            this.roomMemberFrom = new RoomMember();
        }
        this.roomMemberFrom.setRoomMemberId(id);
    }

    public int getToId() {
        return this.roomMemberTo != null ? this.roomMemberTo.getRoomMemberId() : 0;
    }

    public void setToId(int id) {
        if (this.roomMemberTo == null) {
            this.roomMemberTo = new RoomMember();
        }
        this.roomMemberTo.setRoomMemberId(id);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @PrePersist
    @PreUpdate
    private void validateRoomIds() {
        if (this.roomMemberFrom != null && this.roomMemberTo != null &&
                !(this.roomMemberFrom.getRoomId() == this.roomMemberTo.getRoomId())) {
            throw new IllegalArgumentException("room_from_id and room_to_id must be the same.");
        }
    }
}
