package com.example.whoami.repository;

import com.example.whoami.domain.RoomMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomMessageRepository extends JpaRepository<RoomMessage, Integer> {
}
