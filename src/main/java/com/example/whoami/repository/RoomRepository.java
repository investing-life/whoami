package com.example.whoami.repository;

import com.example.whoami.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository  extends JpaRepository<Room, Integer> {
    public Optional<Room> findByName(String name);

    public Optional<Room> findByLink(String link);
}
