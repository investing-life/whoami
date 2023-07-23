package com.example.whoami.repository;

import com.example.whoami.domain.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RoomMemberRepository extends JpaRepository<RoomMember, Integer> {
    Optional<List<RoomMember>> findAllByMember_IndexNumberAndDeleted(int memberId, boolean deleted);

    Optional<List<RoomMember>> findAllByRoom_IndexNumberAndDeleted(int roomId, boolean deleted);

    Optional<RoomMember> findByRoom_linkAndMember_IndexNumberAndDeleted(String roomLink, int memberId, boolean deleted);

    Optional<RoomMember> findByRoom_IndexNumberAndRoomMemberIdAndDeleted(int roomId, int roomMemberId, boolean deleted);

    Optional<RoomMember> findByRoom_linkAndRoomMemberIdAndDeleted(String roomLink, int roomMemberId, boolean deleted);
}
