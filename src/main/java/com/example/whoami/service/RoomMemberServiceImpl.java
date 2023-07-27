package com.example.whoami.service;

import com.example.whoami.domain.Member;
import com.example.whoami.domain.RoomMember;
import com.example.whoami.dto.RoomMemberDTO;
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
    public String findNameByRoomLinkAndIdAndDeleted(String roomLink, int roomMemberId, boolean deleted) {
        // EntityManagerFactory 생성
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myPersistenceUnit, EnvironmentVariables.getProperties()");
        // EntityManager 생성
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        String jpql = "SELECT rm.memberName FROM RoomMember rm WHERE rm.room.link = :roomLink AND rm.roomMemberId = :roomMemberId AND rm.deleted = :deleted";
        Query query = entityManager.createQuery(jpql);
        query.setParameter("roomLink", roomLink);
        query.setParameter("roomMemberId", roomMemberId);
        query.setParameter("deleted", deleted);

        if (query.getResultList().isEmpty()) {
            return null;
        }
        String result = query.getSingleResult().toString();

        // EntityManager 닫기
        entityManager.close();
        // EntityManagerFactory 닫기
        entityManagerFactory.close();

        return result;
    }

    @Override
    public int findMemberIdByRoomLinkAndIdAndDeleted(String roomLink, int roomMemberId, boolean deleted) {
        // EntityManagerFactory 생성
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myPersistenceUnit, EnvironmentVariables.getProperties()");
        // EntityManager 생성
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        String jpql = "SELECT rm.member.indexNumber FROM RoomMember rm WHERE rm.room.link = :roomLink AND rm.roomMemberId = :roomMemberId AND rm.deleted = :deleted";
        Query query = entityManager.createQuery(jpql);
        query.setParameter("roomLink", roomLink);
        query.setParameter("roomMemberId", roomMemberId);
        query.setParameter("deleted", deleted);

        if (query.getResultList().isEmpty()) {
            return 0;
        }
        int result = Integer.parseInt(query.getSingleResult().toString());

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

    @Override
    public boolean startTip(String roomLink, int indexNumber) {
        RoomMember roomMember = roomMemberRepository.findByRoom_linkAndMember_IndexNumberAndDeleted(roomLink, indexNumber, false).get();
        if (roomMember.getQuestionNum() != 0) {
            return false;
        }
        roomMember.setTipPopup(true);
        roomMember.setQuestionNum(1);
        roomMemberRepository.save(roomMember);
        return true;
    }

    @Override
    public RoomMemberDTO findByRoomMemberId(String roomLink, int roomMemberId, boolean deleted) {
        Optional<RoomMember> roomMemberOptional = roomMemberRepository.findByRoom_linkAndRoomMemberIdAndDeleted(roomLink, roomMemberId, deleted);
        if (roomMemberOptional.isEmpty()) {
            return null;
        } else {
            RoomMember roomMember = roomMemberOptional.get();
            RoomMemberDTO roomMemberDTO = new RoomMemberDTO();
            roomMemberDTO.setRoomId(roomMember.getRoomId());
            roomMemberDTO.setMemberId(roomMember.getMemberId());
            roomMemberDTO.setMemberName(roomMember.getMemberName());
            roomMemberDTO.setRoomMemberId(roomMember.getRoomMemberId());
            roomMemberDTO.setQuestionNum(roomMember.getQuestionNum());
            return roomMemberDTO;
        }
    }

    @Override
    public RoomMemberDTO findByMemberID(String roomLink, int memberId, boolean deleted) {
        Optional<RoomMember> roomMemberOptional = roomMemberRepository.findByRoom_linkAndMember_IndexNumberAndDeleted(roomLink, memberId, deleted);
        if (roomMemberOptional.isEmpty()) {
            return null;
        } else {
            RoomMember roomMember = roomMemberOptional.get();
            RoomMemberDTO roomMemberDTO = new RoomMemberDTO();
            roomMemberDTO.setRoomId(roomMember.getRoomId());
            roomMemberDTO.setMemberId(roomMember.getMemberId());
            roomMemberDTO.setMemberName(roomMember.getMemberName());
            roomMemberDTO.setRoomMemberId(roomMember.getRoomMemberId());
            roomMemberDTO.setQuestionNum(roomMember.getQuestionNum());
            return roomMemberDTO;
        }
    }
}

