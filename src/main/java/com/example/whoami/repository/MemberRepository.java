package com.example.whoami.repository;

import com.example.whoami.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    public Optional<Member> findByMemberIdAndDeleted(String memberId, boolean deleted);

    public Optional<Member> findByEmailAndDeleted(String email, boolean deleted);
}
