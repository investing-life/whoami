package com.example.whoami.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "inquiry")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "index_number")
    private long indexNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "message")
    private String message;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "send_time", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime sendTime;

    public int getMemberId() {
        return this.member != null ? member.getIndexNumber() : 0;
    }

    public void setMemberId(int memberId) {
        if (this.member == null) {
            this.member = new Member();
        }
        this.member.setIndexNumber(memberId);
    }
}
