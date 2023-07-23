package com.example.whoami.controller;

import com.example.whoami.domain.RoomMember;
import com.example.whoami.repository.RoomMemberRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    private final RoomMemberRepository roomMemberRepository;
    private final EntityManager entityManager;

    @Autowired
    public TestController(RoomMemberRepository roomMemberRepository, EntityManager entityManager) {
        this.roomMemberRepository = roomMemberRepository;
        this.entityManager = entityManager;
    }

    @GetMapping("/test")
    public String test() {
        RoomMember roomMember = roomMemberRepository.findByRoom_linkAndMember_IndexNumberAndDeleted("BBBBBBBBBB", 2, false).get();
        System.out.println("이름: " + roomMember.getRoom().getName());
        // Clear the persistence context to detach entities
        entityManager.clear();
        RoomMember roomMember1 = roomMemberRepository.findByRoom_linkAndMember_IndexNumberAndDeleted("BBBBBBBBBB", 2, false).get();
        System.out.println("이름: " + roomMember1.getRoom().getName());
        return null;
    }
}
