package com.example.whoami.repository;

import com.example.whoami.domain.RoomMember;
import com.example.whoami.domain.RoomTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomTestRepository extends JpaRepository<RoomTest, Integer> {
    @Query("SELECT rt FROM RoomTest rt " +
            "WHERE rt.roomMemberFrom.room.indexNumber = :roomFromId AND rt.roomMemberFrom.member.indexNumber = :fromId " +
            "AND rt.roomMemberTo.room.indexNumber = :roomToId AND rt.roomMemberTo.member.indexNumber = :toId")
    List<RoomTest> findByRoomMemberFromRoomMemberTo(@Param("roomFromId") int roomFromId, @Param("fromId") int fromId,
                                                    @Param("roomToId") int roomToId, @Param("toId") int toId);

}
