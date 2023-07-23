package com.example.whoami.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "room_member")
public class RoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "index_number")
    private int indexNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "member_name")
    private String memberName;

    @Column(name = "room_member_id")
    private int roomMemberId;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "new_message")
    private boolean newMessage;

    @Column(name = "room_color")
    private String roomColor;

    @Column(name = "tip_popup")
    private boolean tipPopup;

    public int getRoomId() {
        return this.room != null ? room.getIndexNumber() : 0;
    }

    public void setRoomId(int roomId) {
        if (this.room == null) {
            this.room = new Room();
        }
        this.room.setIndexNumber(roomId);
    }

    public int getMemberId() {
        return this.member != null ? member.getIndexNumber() : 0;
    }

    public void setMemberId(int memberId) {
        if (this.member == null) {
            this.member = new Member();
        }
        this.member.setIndexNumber(memberId);
    }

    public int getRoomMemberId() {
        return roomMemberId;
    }

    public void setRoomMemberId(int roomMemberId) {
        this.roomMemberId = roomMemberId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isNewMessage() {
        return newMessage;
    }

    public void setNewMessage(boolean newMessage) {
        this.newMessage = newMessage;
    }

    public String getRoomColor() {
        return roomColor;
    }

    public void setRoomColor(String roomColor) {
        this.roomColor = roomColor;
    }

    public boolean isTipPopup() {
        return tipPopup;
    }

    public void setTipPopup(boolean tipPopup) {
        this.tipPopup = tipPopup;
    }
}
