package com.example.whoami.service;

import com.example.whoami.domain.RoomMember;
import com.example.whoami.domain.RoomMessage;
import com.example.whoami.dto.MessageDTO;
import com.example.whoami.java.EnvironmentVariables;
import com.example.whoami.repository.RoomMemberRepository;
import com.example.whoami.repository.RoomMessageRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoomMessageServiceImpl implements RoomMessageService {

    private final RoomMemberRepository roomMemberRepository;
    private final RoomMessageRepository roomMessageRepository;

    @Autowired
    public RoomMessageServiceImpl(RoomMemberRepository roomMemberRepository, RoomMessageRepository roomMessageRepository) {
        this.roomMemberRepository = roomMemberRepository;
        this.roomMessageRepository = roomMessageRepository;
    }

    public List<MessageDTO> findReceivedMessagesById(int id, String roomLink) {
        List<MessageDTO> messageDTOList = new ArrayList<>();

        // EntityManagerFactory 생성
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myPersistenceUnit", EnvironmentVariables.getProperties());
        // EntityManager 생성
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        // RoomMessage는 Room 엔티티를 참조하는데, RoomMessage 엔티티 자체가 정의되기 전에 JPQL 쿼리에서 사용되어서 문제가 발생 (by chat gpt)
//        String jpql = "SELECT message, sendTime FROM RoomMember a INNER JOIN RoomMessage b " +
//                "On a.roomId = b.roomId AND a.roomMemberId = b.roomMemberTo.roomMemberId INNER JOIN Room c " +
//                "On a.roomId = c.indexNumber WHERE c.link = :roomLink AND a.memberId = :id";

        Instant beforeTime = Instant.now();
        String jpql = "SELECT rm FROM RoomMessage rm " +
                "JOIN FETCH rm.roomMemberTo rmt " +
                "WHERE rmt.room.link = :roomLink AND rmt.member.indexNumber = :id ORDER BY rm.sendTime DESC";

        Query query = entityManager.createQuery(jpql);
        query.setParameter("id", id);
        query.setParameter("roomLink", roomLink);
        query.getResultList();
        Instant afterTime = Instant.now();
        System.out.println("Fetch join - 실행 시간(ms): " + Duration.between(beforeTime, afterTime).toMillis());
        beforeTime = Instant.now();
        jpql = "SELECT rm.message, rm.sendTime, rm.color FROM RoomMessage rm " +
                "JOIN rm.roomMemberTo rmt " +
                "WHERE rmt.room.link = :roomLink AND rmt.member.indexNumber = :id ORDER BY rm.sendTime DESC";
        query = entityManager.createQuery(jpql);
        query.setParameter("id", id);
        query.setParameter("roomLink", roomLink);
        List<Object[]> resultList = query.getResultList();
        afterTime = Instant.now();
        System.out.println("Original join - 실행 시간(ms): " + Duration.between(beforeTime, afterTime).toMillis());

        beforeTime = Instant.now();
        jpql = "SELECT rm.message, rm.sendTime, rm.color FROM RoomMessage rm " +
                "WHERE rm.roomMemberTo.room.link = :roomLink AND rm.roomMemberTo.member.indexNumber = :id Order By rm.sendTime DESC";
        query = entityManager.createQuery(jpql);
        query.setParameter("id", id);
        query.setParameter("roomLink", roomLink);
        query.getResultList();
        afterTime = Instant.now();
        System.out.println("No join - 실행 시간(ms): " + Duration.between(beforeTime, afterTime).toMillis());

        for (Object[] result : resultList) {
            MessageDTO messageDTO = new MessageDTO();
            String message = ((String) result[0]).replace("\r\n", "<br>");
            LocalDateTime sendTime = (LocalDateTime) result[1];
            String color = (String) result[2];
            messageDTO.setMessage(message);
            messageDTO.setSendTime(sendTime);
            messageDTO.setColor(color);
            messageDTOList.add(messageDTO);
        }

        // EntityManager 닫기
        entityManager.close();
        // EntityManagerFactory 닫기
        entityManagerFactory.close();

        return messageDTOList;
    }

    public List<MessageDTO> findSentMessagesById(int id, int toId, String roomLink) {
        List<MessageDTO> messageDTOList = new ArrayList<>();

        // EntityManagerFactory 생성
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myPersistenceUnit", EnvironmentVariables.getProperties());
        // EntityManager 생성
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        String jpql = "SELECT rm.message, rm.sendTime, rm.color FROM RoomMessage rm " +
                "WHERE rm.roomMemberTo.room.link = :roomLink AND rm.roomMemberTo.roomMemberId = :toId AND rm.roomMemberFrom.member.indexNumber = :id";
        Query query = entityManager.createQuery(jpql);
        query.setParameter("id", id);
        query.setParameter("toId", toId);
        query.setParameter("roomLink", roomLink);
        List<Object[]> resultList = query.getResultList();

        for (Object[] result : resultList) {
            MessageDTO messageDTO = new MessageDTO();
            String message = ((String) result[0]).replace("\r\n", "<br>");
            LocalDateTime sendTime = (LocalDateTime) result[1];
            String color = (String) result[2];
            messageDTO.setMessage(message);
            messageDTO.setSendTime(sendTime);
            messageDTO.setColor(color);
            messageDTOList.add(messageDTO);
        }

        // EntityManager 닫기
        entityManager.close();
        // EntityManagerFactory 닫기
        entityManagerFactory.close();

        return messageDTOList;
    }

    public void createMessage(int id, String roomLink, int toId, String message) {
        RoomMessage roomMessage = new RoomMessage();
        Optional<RoomMember> roomMemberOptional = roomMemberRepository.findByRoom_linkAndMember_IndexNumberAndDeleted(roomLink, id, false);
        if (roomMemberOptional.isPresent()) {
            RoomMember roomMember = roomMemberOptional.get();

            Optional<RoomMember> roomMemberToOptional = roomMemberRepository.findByRoom_IndexNumberAndRoomMemberIdAndDeleted(roomMember.getRoomId(), toId, false);

            if (roomMemberToOptional.isPresent()) {
                RoomMember roomMemberTo = roomMemberToOptional.get();
                roomMessage.setRoomMemberFrom(roomMember);
                roomMessage.setRoomMemberTo(roomMemberTo);
                roomMessage.setMessage(message);
                // color
                int r = (int)(Math.random() * 36) + 216;
                int g = (int)(Math.random() * 36) + 216;
                int b = (int)(Math.random() * 36) + 216;
                String color = String.format("#%02X%02X%02X", r, g, b).toUpperCase();

                roomMessage.setColor(color);
                roomMessageRepository.save(roomMessage);
                roomMemberTo.setNewMessage(true);
                roomMemberRepository.save(roomMemberTo);
            }
        }
    }
}
