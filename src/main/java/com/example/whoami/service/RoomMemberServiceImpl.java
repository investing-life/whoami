package com.example.whoami.service;

import com.example.whoami.domain.Member;
import com.example.whoami.domain.RoomMember;
import com.example.whoami.repository.MemberRepository;
import com.example.whoami.repository.RoomMemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoomMemberServiceImpl implements RoomMemberService {

    @Autowired
    private final MemberRepository memberRepository;
    private final RoomMemberRepository roomMemberRepository;

    public RoomMemberServiceImpl(MemberRepository memberRepository, RoomMemberRepository roomMemberRepository) {
        this.memberRepository = memberRepository;
        this.roomMemberRepository = roomMemberRepository;
    }

    @Override
    public void updateRoomMember(String roomLink, int indexNumber, String nickname, String roomColor) {
        Optional<RoomMember> optionalRoomMember = roomMemberRepository.findByRoom_linkAndMember_IndexNumberAndDeleted(roomLink, indexNumber, false);
        if (optionalRoomMember.isPresent()) {
            RoomMember roomMember = optionalRoomMember.get();
            if (nickname != null) {
                roomMember.setMemberName(nickname);
            }
            if (roomColor != null) {
                roomMember.setRoomColor(roomColor.toUpperCase());
            }
            roomMemberRepository.save(roomMember);
        } else {
            // 이름 바꾸는 사이 방을 나가버렸다...?
        }
    }

    @Override
    public String findNameByRoomLinkAndId(String roomLink, int roomMemberId) {
        // EntityManagerFactory 생성
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myPersistenceUnit");
        // EntityManager 생성
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        String jpql = "SELECT rm.memberName FROM RoomMember rm WHERE rm.room.link = :roomLink AND rm.roomMemberId = :roomMemberId";
        Query query = entityManager.createQuery(jpql);
        query.setParameter("roomLink", roomLink);
        query.setParameter("roomMemberId", roomMemberId);

        String result = query.getSingleResult().toString();

        // EntityManager 닫기
        entityManager.close();
        // EntityManagerFactory 닫기
        entityManagerFactory.close();

        return result;
    }

    @Override
    public void readMessage(String roomLink, int indexNumber) {
        RoomMember roomMember = roomMemberRepository.findByRoom_linkAndMember_IndexNumberAndDeleted(roomLink, indexNumber, false).get();
        roomMember.setNewMessage(false);
        roomMemberRepository.save(roomMember);
    }

    @Override
    public void deleteRoomMember(String roomLink, int indexNumber) {
        RoomMember roomMember = roomMemberRepository.findByRoom_linkAndMember_IndexNumberAndDeleted(roomLink, indexNumber, false).get();
        Member member = memberRepository.findByMemberIdAndDeleted("anonymousUser", false).get();
        roomMember.setMember(member);
        roomMember.setDeleted(true);
        roomMemberRepository.save(roomMember);
    }

    @Override
    public void setTipPopup(String roomLink, int indexNumber, boolean value) {
        RoomMember roomMember = roomMemberRepository.findByRoom_linkAndMember_IndexNumberAndDeleted(roomLink, indexNumber, false).get();
        roomMember.setTipPopup(value);
        roomMemberRepository.save(roomMember);
    }
}

