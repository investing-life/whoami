package com.example.whoami.repository;

import com.example.whoami.domain.RoomTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomTestRepository extends JpaRepository<RoomTest, Integer> {
}
