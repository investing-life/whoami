package com.example.whoami.service;

import com.example.whoami.domain.RoomMember;
import com.example.whoami.domain.RoomTest;
import com.example.whoami.dto.TestDTO;
import com.example.whoami.java.EnvironmentVariables;
import com.example.whoami.repository.RoomMemberRepository;
import com.example.whoami.repository.RoomTestRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoomTestServiceImpl implements RoomTestService {

    private final RoomTestRepository roomTestRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final EntityManager entityManager;

    @Autowired
    public RoomTestServiceImpl(RoomTestRepository roomTestRepository, RoomMemberRepository roomMemberRepository, EntityManager entityManager) {
        this.roomTestRepository = roomTestRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.entityManager = entityManager;
    }

    public void createTest(TestDTO testDTO) {
        Optional<RoomMember> roomMemberOptional = roomMemberRepository.findByRoom_linkAndMember_IndexNumberAndDeleted(testDTO.getRoomLink(), testDTO.getIndexNumber(), false);
        if (roomMemberOptional.isPresent()) {
            RoomMember roomMember = roomMemberOptional.get();
            RoomMember roomMemberTo = roomMemberRepository.findByRoom_IndexNumberAndRoomMemberIdAndDeleted(roomMember.getRoomId(), testDTO.getToId(), false).get();

            List<RoomTest> roomTestList = roomTestRepository.findByRoomMemberFromRoomMemberTo(roomMember.getRoomId(), roomMember.getMemberId(), roomMemberTo.getRoomId(), roomMemberTo.getMemberId());

            RoomTest roomTest;
            if (!roomTestList.isEmpty()) {
                roomTest = roomTestList.get(0);
            } else {
                roomTest = new RoomTest();
            }

            roomTest.setRoomMemberFrom(roomMember);
            roomTest.setRoomMemberTo(roomMemberTo);

            roomTest.setOpenness(testDTO.getOpenness());
            roomTest.setConscientiousness(testDTO.getConscientiousness());
            roomTest.setExtraversion(testDTO.getExtraversion());
            roomTest.setAgreeableness(testDTO.getAgreeableness());
            roomTest.setNeuroticism(testDTO.getNeuroticism());

            entityManager.detach(roomMember);
            entityManager.detach(roomMemberTo);

            roomTestRepository.save(roomTest);
        }
    }

    @Override
    public TestDTO findReceivedTestScoresById(int id, String roomLink) {
        TestDTO testDTO = new TestDTO();

        // EntityManagerFactory 생성
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myPersistenceUnit", EnvironmentVariables.getProperties());
        // EntityManager 생성
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        String jpql = "SELECT rt.openness, rt.conscientiousness, rt.extraversion, rt.agreeableness, rt.neuroticism FROM RoomTest rt " +
                "JOIN rt.roomMemberTo rmt " +
                "WHERE rmt.room.link = :roomLink AND rmt.member.indexNumber = :id";
        Query query = entityManager.createQuery(jpql);
        query.setParameter("id", id);
        query.setParameter("roomLink", roomLink);
        List<Object[]> resultList = query.getResultList();

        float openness = 0;
        float conscientiousness = 0;
        float extraversion = 0;
        float agreeableness = 0;
        float neuroticism = 0;

        if (resultList.size() >= 2) {
            for (Object[] result : resultList) {
                openness += (float) result[0];
                conscientiousness += (float) result[1];
                extraversion += (float) result[2];
                agreeableness += (float) result[3];
                neuroticism += (float) result[4];
            }
            testDTO.setOpenness(openness / resultList.size());
            testDTO.setConscientiousness(conscientiousness / resultList.size());
            testDTO.setExtraversion(extraversion / resultList.size());
            testDTO.setAgreeableness(agreeableness / resultList.size());
            testDTO.setNeuroticism(neuroticism / resultList.size());
        }

        // EntityManager 닫기
        entityManager.close();
        // EntityManagerFactory 닫기
        entityManagerFactory.close();

        return testDTO;
    }

    @Override
    public List<Integer> getTestList(int id, String roomLink) {
        // EntityManagerFactory 생성
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myPersistenceUnit", EnvironmentVariables.getProperties());
        // EntityManager 생성
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        String jpql = "SELECT DISTINCT(rt.roomMemberTo.roomMemberId) FROM RoomTest rt " +
                "JOIN rt.roomMemberFrom rmf " +
                "WHERE rmf.room.link = :roomLink AND rmf.member.indexNumber = :id";
        Query query = entityManager.createQuery(jpql);
        query.setParameter("id", id);
        query.setParameter("roomLink", roomLink);
        List<Integer> indexNumberList = query.getResultList();

        // EntityManager 닫기
        entityManager.close();
        // EntityManagerFactory 닫기
        entityManagerFactory.close();

        return indexNumberList;
    }

}
