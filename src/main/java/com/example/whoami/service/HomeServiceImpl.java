package com.example.whoami.service;

import com.example.whoami.domain.Room;
import com.example.whoami.domain.RoomMember;
import com.example.whoami.dto.RoomInfoDTO;
import com.example.whoami.java.IdGenerator;
import com.example.whoami.java.RandomAddressGenerator;
import com.example.whoami.repository.RoomMemberRepository;
import com.example.whoami.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class HomeServiceImpl implements HomeService {

    private final RoomMemberRepository roomMemberRepository;
    private final RoomRepository roomRepository;
    private final RandomAddressGenerator randomAddressGenerator;
    private final IdGenerator idGenerator;

    @Autowired
    public HomeServiceImpl(RoomMemberRepository roomMemberRepository, RoomRepository roomRepository, RandomAddressGenerator randomAddressGenerator, IdGenerator idGenerator) {
        this.roomMemberRepository = roomMemberRepository;
        this.roomRepository = roomRepository;
        this.randomAddressGenerator = randomAddressGenerator;
        this.idGenerator = idGenerator;
    }

    @Override
    public List<RoomInfoDTO> getRoomByMemberId(int memberId) {
        List<RoomInfoDTO> dtoList = new ArrayList<>();

        Optional<List<RoomMember>> entityListOptional = roomMemberRepository.findAllByMember_IndexNumberAndDeleted(memberId, false);
        List<String> roomList = new ArrayList<>();
        if (entityListOptional.isPresent()){
            List<RoomMember> entityList = entityListOptional.get();
            for (RoomMember entity : entityList) {
                RoomInfoDTO dto = new RoomInfoDTO();
                Optional<Room> roomOptional = roomRepository.findById(entity.getRoomId());
                if (roomOptional.isPresent()) {
                    Room room = roomOptional.get();
                    dto.setRoomName(room.getName());
                    dto.setRoomLink(room.getLink());

                    // room_member DB에서 room_id로 member_name list 가져오기
                    entityListOptional = roomMemberRepository.findAllByRoom_IndexNumberAndDeleted(room.getIndexNumber(), false);
                    Map<Integer, String> tuple = new HashMap<>();
                    if (entityListOptional.isPresent()) {
                        List<RoomMember> entityList2 = entityListOptional.get();
                        for (RoomMember entity2 : entityList2) {
                            if (entity2.getMemberId() == memberId) {
                                dto.setMyName(entity.getMemberName());
                                dto.setNewMessage(entity.isNewMessage());
                                dto.setRoomColor(entity.getRoomColor());
                            }
                        }
                        for (RoomMember entity2 : entityList2) {
                            if (entity2.getMemberId() != memberId) {
                                if (tuple.size() >= 2) {
                                    tuple.put(99999999, "...");
                                    break;
                                }
                                tuple.put(entity2.getRoomMemberId(), entity2.getMemberName());
                            }
                        }
                    }
                    dto.setMemberList(tuple);
                }
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    @Override
    public boolean checkDuplicateRoomName(String roomName) {
        Optional<Room> existingRoom = roomRepository.findByName(roomName);
        return existingRoom.isPresent();
    }

    @Override
    @Transactional
    public String createRoom(String name, String nickname, String roomColor) {
        Room room = new Room();
        room.setName(name);

        String newAddress = new String();
        while (true) {
            newAddress = randomAddressGenerator.generateRandomAddress(15);
            Optional<Room> roomOptional = roomRepository.findByLink(newAddress);
            if (roomOptional.isEmpty()) { break; }
        }
        room.setLink(newAddress);
        room.setCreateDate(LocalDate.now());
        Room savedRoom = roomRepository.save(room);
        
        // 방에 사람이 들어갔다는 정보 저장
        RoomMember roomMember = new RoomMember();
        roomMember.setMemberId(idGenerator.getIdFromSession());
        roomMember.setRoomId(savedRoom.getIndexNumber());
        roomMember.setMemberName(nickname);
        roomMember.setRoomMemberId(1);
        roomMember.setRoomColor(roomColor.toUpperCase());
        roomMemberRepository.save(roomMember);

        return newAddress;
    }
}
