package com.example.whoami.java;

import com.example.whoami.domain.History;
import com.example.whoami.domain.Member;
import com.example.whoami.domain.RoomMember;
import com.example.whoami.repository.HistoryRepository;
import com.example.whoami.repository.MemberRepository;
import com.example.whoami.repository.RoomMemberRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class Scheduler {

    private final MemberRepository memberRepository;
    private final HistoryRepository historyRepository;
    private final RoomMemberRepository roomMemberRepository;

    public Scheduler(MemberRepository memberRepository, HistoryRepository historyRepository, RoomMemberRepository roomMemberRepository) {
        this.memberRepository = memberRepository;
        this.historyRepository = historyRepository;
        this.roomMemberRepository = roomMemberRepository;
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정(0시 0분 0초)에 실행
    @Transactional
    public void midnightScheduler() {
        List<Member> memberList = memberRepository.findAll();
        int count = 0;
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        for (Member member : memberList) {
            if (member.getLastAccessTime().toLocalDate().isEqual(yesterday)) {
                count++;
            }
        }
        History history = new History(yesterday, count);
        historyRepository.save(history);

        // difference가 10보다 작거나 같으면 켜기 (10은 종료된 첫째 날)
        List<RoomMember> roomMemberList = roomMemberRepository.findAll();
        for (RoomMember roomMember : roomMemberList) {
            long difference = ChronoUnit.DAYS.between(roomMember.getRoom().getCreateDate(), LocalDate.now());
            System.out.println(roomMember.getMemberName() + ", " + difference);
            if (!roomMember.isDeleted() && (difference <= 10)) {
                roomMember.setTipPopup(true);
                roomMemberRepository.save(roomMember);
            }
        }
    }
}
