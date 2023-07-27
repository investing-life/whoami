package com.example.whoami.service;

import com.example.whoami.domain.Room;
import com.example.whoami.domain.RoomMember;
import com.example.whoami.dto.RoomInfoDTO;
import com.example.whoami.exception.DuplicateRoomMemberException;
import com.example.whoami.exception.InvalidRoomLinkException;
import com.example.whoami.java.EnvironmentVariables;
import com.example.whoami.java.IdGenerator;
import com.example.whoami.repository.RoomMemberRepository;
import com.example.whoami.repository.RoomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoomServiceImpl implements RoomService {

    private final IdGenerator idGenerator;
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;

    @Autowired
    public RoomServiceImpl(IdGenerator idGenerator, RoomRepository roomRepository, RoomMemberRepository roomMemberRepository) {
        this.idGenerator = idGenerator;
        this.roomRepository = roomRepository;
        this.roomMemberRepository = roomMemberRepository;
    }

    public boolean roomMemberExist(String roomLink) {

        // EntityManagerFactory 생성
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myPersistenceUnit", EnvironmentVariables.getProperties());

        // EntityManager 생성
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // 해당 방에 가입되어있는지 확인
        String jpql = "SELECT COUNT(rm.indexNumber) FROM RoomMember rm WHERE rm.member.indexNumber = :memberId AND rm.room.indexNumber = (SELECT r.indexNumber FROM Room r WHERE r.link = :roomLink) AND rm.deleted = false";
        Query query = entityManager.createQuery(jpql);
        query.setParameter("memberId", idGenerator.getIdFromSession());
        query.setParameter("roomLink", roomLink);
        int count = ((Long) query.getSingleResult()).intValue();
        // EntityManager 닫기
        entityManager.close();
        // EntityManagerFactory 닫기
        entityManagerFactory.close();

        if (count > 1) {
            throw new DuplicateRoomMemberException("Duplicate room member");
        } else return count > 0;
    }

    public RoomInfoDTO getRoomByLink(String roomLink) {
        int indexNumber = idGenerator.getIdFromSession();
        RoomInfoDTO dto = new RoomInfoDTO();
        // roomLink로 IndexNumber 가져오기
        Optional<Room> roomOptional = roomRepository.findByLink(roomLink);
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            dto.setRoomName(room.getName());
            dto.setRoomLink(roomLink);
            dto.setCreateDate(room.getCreateDate());

            // room_member DB에서 room_id로 member_name list 가져오기
            Optional<List<RoomMember>> roomMemberOptional = roomMemberRepository.findAllByRoom_IndexNumberAndDeleted(room.getIndexNumber(), false);
            Map<Integer, String> tuple = new HashMap<>();
            if (roomMemberOptional.isPresent()) {
                List<RoomMember> entityList = roomMemberOptional.get();
                for (RoomMember entity : entityList) {
                    if (entity.getMemberId() == indexNumber) {
                        dto.setMyName(entity.getMemberName());
                        dto.setRoomColor(entity.getRoomColor());
                        dto.setTipPopup(entity.isTipPopup());
                    } else {
                        tuple.put(entity.getRoomMemberId(), entity.getMemberName());
                    }
                }
            }
            dto.setMemberList(tuple);

            return dto;
        } else {
            throw new InvalidRoomLinkException("Invalid Room Link");
        }
    }

    public void createRoomMember(String roomLink, int indexNumber, String nickname, String roomColor) {
        RoomMember roomMember = new RoomMember();
        int roomId = roomRepository.findByLink(roomLink).get().getIndexNumber();
        roomMember.setRoomId(roomId);
        roomMember.setMemberId(indexNumber);
        roomMember.setMemberName(nickname);
        roomMember.setRoomColor(roomColor.toUpperCase());
        roomMember.setTipPopup(true);
        roomMember.setQuestionNum(0);

        // EntityManagerFactory 생성
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myPersistenceUnit", EnvironmentVariables.getProperties());
        // EntityManager 생성
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        String jpql = "SELECT MAX(rm.roomMemberId) FROM RoomMember rm WHERE rm.room.indexNumber = :roomId";
        Query query = entityManager.createQuery(jpql);
        query.setParameter("roomId", roomId);

        if (query.getSingleResult() == null) {
            roomMember.setRoomMemberId(1);
        } else {
            roomMember.setRoomMemberId(((int) query.getSingleResult()) + 1);
        }

        // EntityManager 닫기
        entityManager.close();
        // EntityManagerFactory 닫기
        entityManagerFactory.close();

        roomMemberRepository.save(roomMember);
    }

    public void updateRoomByLink(String roomLink, String title) {
        Room room = roomRepository.findByLink(roomLink).get();
        room.setName(title);
        roomRepository.save(room);
    }

}
