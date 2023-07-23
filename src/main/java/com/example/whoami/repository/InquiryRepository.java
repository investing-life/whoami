package com.example.whoami.repository;

import com.example.whoami.domain.Inquiry;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {
    public Optional<List<Inquiry>> findAllByMember_IndexNumber(int indexNumber);

    @Query("SELECT i FROM Inquiry i JOIN FETCH i.member")
    List<Inquiry> findAllWithMemberFetch(Sort sort);
}
