package com.example.whoami.repository;

import com.example.whoami.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface HistoryRepository extends JpaRepository<History, LocalDate> {

}
