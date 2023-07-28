package com.example.whoami.controller;

import com.example.whoami.dto.*;
import com.example.whoami.exception.DuplicateRoomMemberException;
import com.example.whoami.exception.InvalidRoomLinkException;
import com.example.whoami.exception.NotJoinedRoomException;
import com.example.whoami.java.IdGenerator;
import com.example.whoami.service.*;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

@Controller
public class RoomController {

    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);
    private final EntityManager entityManager;
    private final MemberService memberService;
    private final RoomService roomService;
    private final RoomMemberService roomMemberService;
    private final IdGenerator idGenerator;
    private final RoomMessageService roomMessageService;
    private final RoomTestService roomTestService;
    private final HomeService homeService;

    @Autowired
    public RoomController(EntityManager entityManager, MemberService memberService, RoomService roomService, RoomMemberService roomMemberService, IdGenerator idGenerator, RoomMessageService roomMessageService, RoomTestService roomTestService, HomeService homeService) {
        this.entityManager = entityManager;
        this.memberService = memberService;
        this.roomService = roomService;
        this.roomMemberService = roomMemberService;
        this.idGenerator = idGenerator;
        this.roomMessageService = roomMessageService;
        this.roomTestService = roomTestService;
        this.homeService = homeService;
    }

    @GetMapping("/home/rooms/{link}")
    public String room(Model model, @PathVariable("link") String link) {
        try {
            int indexNumber = idGenerator.getIdFromSession();

            // message read
            roomMemberService.readMessage(link, indexNumber);

            // accessed
            model.addAttribute("member", memberService.getMemberInfoAndUpdateLastAccessTime(indexNumber));
            RoomInfoDTO roomInfoDTO = roomService.getRoomByLink(link);
            model.addAttribute("room", roomInfoDTO);
            List<Integer> indexNumberList = roomTestService.getTestList(indexNumber, link);
            List<Boolean> testList = new ArrayList<>();
            for (int roomMemberId : roomInfoDTO.getMemberList().keySet()) {
                if (indexNumberList.contains(roomMemberId)) {
                    testList.add(true);
                } else {
                    testList.add(false);
                }
            }
            model.addAttribute("testList", testList);
            model.addAttribute("messages", roomMessageService.findReceivedMessagesById(indexNumber, link));
            model.addAttribute("testScores", roomTestService.findReceivedTestScoresById(indexNumber, link));

            return "rooms/room";
        } catch (NotJoinedRoomException e) {
            try {
                roomService.getRoomByLink(link);
            } catch (InvalidRoomLinkException ee) {
                logger.error("Error occurred while entering room");
                model.addAttribute("errorMessageTitle", "방 진입 실패");
                model.addAttribute("errorMessageContent", "유효하지 않은 주소입니다.");
                return "errorPage";
            }
            return "redirect:/home/rooms/" + link + "/join";
        }
    }

    @GetMapping("/home/rooms/{link}/join")
    public String joinRoom(Model model, @PathVariable("link") String link) {
        boolean alreadyJoined = false;
        try {
            alreadyJoined = roomService.roomMemberExist(link);
        } catch (DuplicateRoomMemberException e) {
            logger.error("Error occurred while entering room");
        }
        if (alreadyJoined) {
            return "redirect:/home/rooms/" + link;
        }
        RoomInfoDTO roomInfoDTO;
        try {
            roomInfoDTO = roomService.getRoomByLink(link);
        } catch (InvalidRoomLinkException e) {
            logger.error("Error occurred while entering room");
            return "redirect:/error-page";
        }
        model.addAttribute("room", roomInfoDTO);
        return "rooms/join-room";
    }

    @PostMapping("/home/rooms/{link}/join")
    public String joinRoom(@PathVariable("link") String link, @RequestParam("nickname") String nickname, @RequestParam("color") String color, Model model) {
        // 이미 가입한 유저라면 redirect
        boolean alreadyJoined = false;
        try {
            alreadyJoined = roomService.roomMemberExist(link);
        } catch (DuplicateRoomMemberException e) {
            logger.error("Error occurred while entering room");
        }
        if (alreadyJoined) {
            return "redirect:/home/rooms/" + link;
        }
        
        // 유효성 검사
        if (nickname.length() < 1 || nickname.length() > 15) {
            model.addAttribute("errorMessageTitle", "방 가입 실패");
            model.addAttribute("errorMessageContent", "이름의 길이는 1 ~ 15 사이여야 합니다.");
            return "errorPage";
        } else if (!Objects.equals(nickname.trim(), nickname)) {
            model.addAttribute("errorMessageTitle", "방 가입 실패");
            model.addAttribute("errorMessageContent", "이름이 공백으로 시작하거나 끝나면 안 됩니다.");
            return "errorPage";
        }
        if (!Pattern.matches("^#[0-9A-Fa-f]{6}$", color)) {
            model.addAttribute("errorMessageTitle", "방 가입 실패");
            model.addAttribute("errorMessageContent", "적절하지 않은 색상 코드입니다.");
            return "errorPage";
        }

        int indexNumber = idGenerator.getIdFromSession();
        roomService.createRoomMember(link, indexNumber, nickname, color);
        return "redirect:/home/rooms/" + link;
    }

    @PostMapping("/home/rooms/{link}/title")
    public String title(@PathVariable("link") String link, @RequestParam("title") String title, Model model) {
        boolean alreadyJoined = true;
        try {
            alreadyJoined = roomService.roomMemberExist(link);
        } catch (DuplicateRoomMemberException e) {
            logger.error("Error occurred while entering room");
        }
        if (!alreadyJoined) {
            try {
                roomService.getRoomByLink(link);
            } catch (InvalidRoomLinkException e) {
                logger.error("Error occurred while entering room");
                model.addAttribute("errorMessageTitle", "방 진입 실패");
                model.addAttribute("errorMessageContent", "유효하지 않은 주소입니다.");
                return "errorPage";
            }
            return "redirect:/home/rooms/" + link + "/join";
        }

        // 유효성 검사
        if (title.length() < 1 || title.length() > 15) {
            model.addAttribute("errorMessageTitle", "방 제목 변경 실패");
            model.addAttribute("errorMessageContent", "방 이름의 길이는 1 ~ 15 사이여야 합니다.");
            return "errorPage";
        } else if (!Objects.equals(title.trim(), title)) {
            model.addAttribute("errorMessageTitle", "방 제목 변경 실패");
            model.addAttribute("errorMessageContent", "방 이름이 공백으로 시작하거나 끝나면 안 됩니다.");
            return "errorPage";
        } else if (homeService.checkDuplicateRoomName(title)) {
            model.addAttribute("errorMessageTitle", "방 제목 변경 실패");
            model.addAttribute("errorMessageContent", "중복된 방 이름입니다.");
            return "errorPage";
        }

        roomService.updateRoomByLink(link, title);
        return "redirect:/home/rooms/" + link;
    }

    @PostMapping("/home/rooms/{link}/name")
    public String name(@PathVariable("link") String link, @RequestParam("nickname") String nickname, Model model) {
        boolean alreadyJoined = true;
        try {
            alreadyJoined = roomService.roomMemberExist(link);
        } catch (DuplicateRoomMemberException e) {
            logger.error("Error occurred while entering room");
        }
        if (!alreadyJoined) {
            try {
                roomService.getRoomByLink(link);
            } catch (InvalidRoomLinkException e) {
                logger.error("Error occurred while entering room");
                model.addAttribute("errorMessageTitle", "방 진입 실패");
                model.addAttribute("errorMessageContent", "유효하지 않은 주소입니다.");
                return "errorPage";
            }
            return "redirect:/home/rooms/" + link + "/join";
        }

        // 유효성 검사
        if (nickname.length() < 1 || nickname.length() > 15) {
            model.addAttribute("errorMessageTitle", "이름 변경 실패");
            model.addAttribute("errorMessageContent", "이름의 길이는 1 ~ 15 사이여야 합니다.");
            return "errorPage";
        } else if (!Objects.equals(nickname.trim(), nickname)) {
            model.addAttribute("errorMessageTitle", "이름 변경 실패");
            model.addAttribute("errorMessageContent", "이름이 공백으로 시작하거나 끝나면 안 됩니다.");
            return "errorPage";
        }

        int indexNumber = idGenerator.getIdFromSession();
        roomMemberService.updateRoomMember(link, indexNumber, nickname, null);
        return "redirect:/home/rooms/" + link;
    }

    @GetMapping("/home/rooms/{link}/profile")
    public String profile(Model model, @PathVariable("link") String link) {
        boolean alreadyJoined = true;
        try {
            alreadyJoined = roomService.roomMemberExist(link);
        } catch (DuplicateRoomMemberException e) {
            logger.error("Error occurred while entering room");
        }
        if (!alreadyJoined) {
            try {
                roomService.getRoomByLink(link);
            } catch (InvalidRoomLinkException e) {
                logger.error("Error occurred while entering room");
                model.addAttribute("errorMessageTitle", "방 진입 실패");
                model.addAttribute("errorMessageContent", "유효하지 않은 주소입니다.");
                return "errorPage";
            }
            return "redirect:/home/rooms/" + link + "/join";
        }

        int indexNumber = idGenerator.getIdFromSession();
        model.addAttribute("member", memberService.getMemberInfo(indexNumber));
        model.addAttribute("room", roomService.getRoomByLink(link));
        model.addAttribute("link", link);
        model.addAttribute("testScores", roomTestService.findReceivedTestScoresById(indexNumber, link));
        return "rooms/profile";
    }

    @GetMapping("/home/rooms/{link}/test")
    public String test(Model model, @PathVariable("link") String link) {
        boolean alreadyJoined = true;
        try {
            alreadyJoined = roomService.roomMemberExist(link);
        } catch (DuplicateRoomMemberException e) {
            logger.error("Error occurred while entering room");
        }
        if (!alreadyJoined) {
            try {
                roomService.getRoomByLink(link);
            } catch (InvalidRoomLinkException e) {
                logger.error("Error occurred while entering room");
                model.addAttribute("errorMessageTitle", "방 진입 실패");
                model.addAttribute("errorMessageContent", "유효하지 않은 주소입니다.");
                return "errorPage";
            }
            return "redirect:/home/rooms/" + link + "/join";
        }

        model.addAttribute("link", link);
        return "rooms/test";
    }

    @PostMapping("/home/rooms/{link}/test")
    @ResponseBody
    public Map<String, String> test(@PathVariable("link") String link, @RequestParam("sex") String sex, @RequestParam("age") String age,
                                    @RequestParam("testResult") String testResult, @RequestParam("openness") float openness,
                                    @RequestParam("conscientiousness") float conscientiousness, @RequestParam("extraversion") float extraversion,
                                    @RequestParam("agreeableness") float agreeableness, @RequestParam("neuroticism") float neuroticism) {
        boolean alreadyJoined = true;
        try {
            alreadyJoined = roomService.roomMemberExist(link);
        } catch (DuplicateRoomMemberException e) {
            logger.error("Error occurred while entering room");
        }
        if (!alreadyJoined) {
            try {
                roomService.getRoomByLink(link);
            } catch (InvalidRoomLinkException e) {
                logger.error("Error occurred while entering room");
                return null;
            }
            return null;
        }
        // 파라미터 유효성 검사
        if (!Objects.equals(sex, "남") && !Objects.equals(sex, "여")) {
            return null;
        }
        String[] validAgeGroups = {"10대 이하", "20대", "30대", "40대", "50대", "60대 이상"};
        boolean validAge = false;
        // 입력된 문자열이 유효한 연령대 문자열인지 확인
        for (String validAgeGroup : validAgeGroups) {
            if (validAgeGroup.equals(age)) {
                validAge = true;
                break;
            }
        }
        if (!validAge) {
            return null;
        }
        if (!Pattern.matches("^[1-5]{56}$", testResult)) {
            return null;
        } else if (openness < 0 || openness > 5 || conscientiousness < 0 || conscientiousness > 5 || extraversion < 0 || extraversion > 5 || agreeableness < 0 || agreeableness > 5 || neuroticism < 0 || neuroticism > 5) {
            return null;
        }

        MemberDTO dto = new MemberDTO();
        dto.setIndexNumber(idGenerator.getIdFromSession());
        dto.setSex(sex);
        dto.setAge(age);
        dto.setOpenness(openness);
        dto.setConscientiousness(conscientiousness);
        dto.setExtraversion(extraversion);
        dto.setAgreeableness(agreeableness);
        dto.setNeuroticism(neuroticism);
        dto.setTestResult(testResult);
        memberService.updateMember(dto);
        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", "/home/rooms/" + link + "/profile");
        return response;
    }

    @GetMapping("/home/rooms/{link}/{toId}/test")
    public String test(Model model, @PathVariable("link") String link, @PathVariable("toId") int toId) {
        boolean alreadyJoined = true;
        try {
            alreadyJoined = roomService.roomMemberExist(link);
        } catch (DuplicateRoomMemberException e) {
            logger.error("Error occurred while entering room");
        }
        if (!alreadyJoined) {
            try {
                roomService.getRoomByLink(link);
            } catch (InvalidRoomLinkException e) {
                logger.error("Error occurred while entering room");
                model.addAttribute("errorMessageTitle", "방 진입 실패");
                model.addAttribute("errorMessageContent", "유효하지 않은 주소입니다.");
                return "errorPage";
            }
            return "redirect:/home/rooms/" + link + "/join";
        }
        // toId 유효성 검사
        String toName = roomMemberService.findNameByRoomLinkAndIdAndDeleted(link, toId, false);
        if (toName == null) {
            model.addAttribute("errorMessageTitle", "방 진입 실패");
            model.addAttribute("errorMessageContent", "유효하지 않은 주소입니다.");
            return "errorPage";
        }
        if (roomMemberService.findMemberIdByRoomLinkAndIdAndDeleted(link, toId, false) == idGenerator.getIdFromSession()) {
            model.addAttribute("errorMessageTitle", "방 진입 실패");
            model.addAttribute("errorMessageContent", "자신은 테스트할 수 없습니다.");
            return "errorPage";
        }

        model.addAttribute("link", link);
        model.addAttribute("toId", toId);
        model.addAttribute("toName", toName);
        return "rooms/friends/test";
    }

    @PostMapping("/home/rooms/{link}/{toId}/test")
    @ResponseBody
    public Map<String, String> test(@PathVariable("link") String link, @PathVariable("toId") int toId, @RequestParam("openness") float openness,
                                    @RequestParam("conscientiousness") float conscientiousness, @RequestParam("extraversion") float extraversion,
                                    @RequestParam("agreeableness") float agreeableness, @RequestParam("neuroticism") float neuroticism) {
        boolean alreadyJoined = true;
        try {
            alreadyJoined = roomService.roomMemberExist(link);
        } catch (DuplicateRoomMemberException e) {
            logger.error("Error occurred while entering room");
        }
        if (!alreadyJoined) {
            try {
                roomService.getRoomByLink(link);
            } catch (InvalidRoomLinkException e) {
                logger.error("Error occurred while entering room");
                return null;
            }
            return null;
        }

        // toId 유효성 검사
        String toName = roomMemberService.findNameByRoomLinkAndIdAndDeleted(link, toId, false);
        if (toName == null) {
            return null;
        }
        if (roomMemberService.findMemberIdByRoomLinkAndIdAndDeleted(link, toId, false) == idGenerator.getIdFromSession()) {
            return null;
        }
        // 파라미터 유효성 검사
        if (openness < 0 || openness > 5 || conscientiousness < 0 || conscientiousness > 5 || extraversion < 0 || extraversion > 5 || agreeableness < 0 || agreeableness > 5 || neuroticism < 0 || neuroticism > 5) {
            return null;
        }

        TestDTO dto = new TestDTO();

        dto.setRoomLink(link);
        dto.setIndexNumber(idGenerator.getIdFromSession());
        dto.setToId(toId);
        dto.setOpenness(openness);
        dto.setConscientiousness(conscientiousness);
        dto.setExtraversion(extraversion);
        dto.setAgreeableness(agreeableness);
        dto.setNeuroticism(neuroticism);
        roomTestService.createTest(dto);
        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", "/home/rooms/" + link);
        return response;
    }

    @GetMapping("/home/rooms/{link}/message")
    public String message(Model model, @PathVariable("link") String link) {
        boolean alreadyJoined = true;
        try {
            alreadyJoined = roomService.roomMemberExist(link);
        } catch (DuplicateRoomMemberException e) {
            logger.error("Error occurred while entering room");
        }
        if (!alreadyJoined) {
            try {
                roomService.getRoomByLink(link);
            } catch (InvalidRoomLinkException e) {
                logger.error("Error occurred while entering room");
                model.addAttribute("errorMessageTitle", "방 진입 실패");
                model.addAttribute("errorMessageContent", "유효하지 않은 주소입니다.");
                return "errorPage";
            }
            return "redirect:/home/rooms/" + link + "/join";
        }

        model.addAttribute("link", link);

        // 메세지 가공 (wrapper는 날짜 저장용)
        List<MessageDTO> messageDTOList = roomMessageService.findReceivedMessagesById(idGenerator.getIdFromSession(), link);
        List<MessageDTO> messageDTOListNew = new ArrayList<>();
        LocalDate previousDate = null;
        for (MessageDTO messageDTO : messageDTOList) {
            LocalDate currentDate = messageDTO.getSendTime().toLocalDate();
            if (previousDate == null || !previousDate.equals(currentDate)) {
                MessageDTO messageDTOTemp = new MessageDTO();
                messageDTOTemp.setWrapper(true);
                messageDTOTemp.setSendTime(messageDTO.getSendTime());
                messageDTOListNew.add(messageDTOTemp);
                previousDate = currentDate;
            }
            messageDTO.setWrapper(false);
            messageDTOListNew.add(messageDTO);
        }

        model.addAttribute("messages", messageDTOListNew);

        int indexNumber = idGenerator.getIdFromSession();

        // accessed
        memberService.updateLastAccessTime(indexNumber, LocalDateTime.now());
        // message read
        roomMemberService.readMessage(link, indexNumber);

        return "rooms/message";
    }

    @GetMapping("/home/rooms/{link}/{toId}/message")
    public String message(Model model, @PathVariable("link") String link, @PathVariable("toId") int toId) {
        boolean alreadyJoined = true;
        try {
            alreadyJoined = roomService.roomMemberExist(link);
        } catch (DuplicateRoomMemberException e) {
            logger.error("Error occurred while entering room");
        }
        if (!alreadyJoined) {
            try {
                roomService.getRoomByLink(link);
            } catch (InvalidRoomLinkException e) {
                logger.error("Error occurred while entering room");
                model.addAttribute("errorMessageTitle", "방 진입 실패");
                model.addAttribute("errorMessageContent", "유효하지 않은 주소입니다.");
                return "errorPage";
            }
            return "redirect:/home/rooms/" + link + "/join";
        }
        // toId 유효성 검사
        RoomMemberDTO toRoomMemberDTO = roomMemberService.findByRoomMemberId(link, toId, false);
        if (toRoomMemberDTO == null) {
            model.addAttribute("errorMessageTitle", "방 진입 실패");
            model.addAttribute("errorMessageContent", "유효하지 않은 주소입니다.");
            return "errorPage";
        }
        int indexNumber = idGenerator.getIdFromSession();
        if (toRoomMemberDTO.getMemberId() == indexNumber) {
            model.addAttribute("errorMessageTitle", "방 진입 실패");
            model.addAttribute("errorMessageContent", "자신에게 메세지를 전송할 수 없습니다.");
            return "errorPage";
        }

        List<MessageDTO> messageDTOList = roomMessageService.findSentMessagesById(indexNumber, toId, link);
        List<MessageDTO> messageDTOListNew = new ArrayList<>();
        LocalDate previousDate = null;
        for (MessageDTO messageDTO : messageDTOList) {
            LocalDate currentDate = messageDTO.getSendTime().toLocalDate();
            if (previousDate == null || !previousDate.equals(currentDate)) {
                MessageDTO messageDTOTemp = new MessageDTO();
                messageDTOTemp.setWrapper(true);
                messageDTOTemp.setSendTime(messageDTO.getSendTime());
                messageDTOListNew.add(messageDTOTemp);
                previousDate = currentDate;
            }
            messageDTO.setWrapper(false);
            messageDTOListNew.add(messageDTO);
        }

        model.addAttribute("messages", messageDTOListNew);
        model.addAttribute("room", roomService.getRoomByLink(link));
        model.addAttribute("link", link);
        model.addAttribute("toId", toId);
        model.addAttribute("toName", toRoomMemberDTO.getMemberName());
        model.addAttribute("messages", messageDTOListNew);

        // 오늘의 Tip
        List<String> todayTipList = new ArrayList<>();
        todayTipList.add("친구의 첫인상은 어땠나요? 처음 봤을 때의 느낌을 적어주세요.");
        todayTipList.add("친구를 떠올리면 어떤 단어가 생각나시나요? 세 단어로 친구를 표현해 주세요!");
        todayTipList.add("더 훌륭한 친구가 되기 위해서 이런 점을 개선했으면 좋겠다고 생각하는 부분이 있나요?");
        todayTipList.add("최고가 아니어도 괜찮습니다. 친구가 가장 잘 하는 것은 무엇인가요?");
        todayTipList.add("요즘 친구의 고민이 있나요? 친구에게 응원의 한마디를 보내주세요!");
        todayTipList.add("친구는 어떤 가치관을 갖고 있는 사람처럼 보이나요?");
        todayTipList.add("친구와 함께했던 가장 즐거웠던 기억을 적어주세요.");
        todayTipList.add("친구에 대해 가장 궁금한 것은 무엇인가요? 다음 모임에 말해줄 지도 몰라요!");

        Collections.shuffle(todayTipList, new java.util.Random(link.substring(0, 10).hashCode()));
        todayTipList.add("친구에게 감동받았던 적이 있다면 고마움을 표현해 보세요!");

        RoomMemberDTO roomMemberDTO = roomMemberService.findByMemberID(link, indexNumber, false);

        int difference = roomMemberDTO.getQuestionNum();

        if (difference > todayTipList.size()) {
            model.addAttribute("todayTip", null);
        } else if(difference == 0) {
            model.addAttribute("todayTip", null);
        } else {
            model.addAttribute("todayTip", todayTipList.get(difference - 1));
        }
        model.addAttribute("difference", difference);

        // 팝업 다시 안 뜨게
        entityManager.clear();
        roomMemberService.setTipPopup(link, idGenerator.getIdFromSession(), false);

        return "rooms/friends/message";
    }

    @PostMapping("/home/rooms/{link}/{toId}/message/tip")
    public ResponseEntity<?> startTip(@PathVariable("link") String link, @PathVariable("toId") int toId) {
        boolean alreadyJoined = true;
        try {
            alreadyJoined = roomService.roomMemberExist(link);
        } catch (DuplicateRoomMemberException e) {
            logger.error("Error occurred while entering room");
        }
        if (!alreadyJoined) {
            try {
                roomService.getRoomByLink(link);
            } catch (InvalidRoomLinkException e) {
                logger.error("Error occurred while entering room");
                return ResponseEntity.badRequest().body("Failed to start tip: Room Not Exist");
            }
            return ResponseEntity.badRequest().body("Failed to start tip: Not Belonged");
        }

        if (!roomMemberService.startTip(link, idGenerator.getIdFromSession())) {
            return ResponseEntity.badRequest().body("Failed to start tip: Already in progress");
        } else {
            return ResponseEntity.ok().build();
        }
    }

    @PostMapping("/home/rooms/{link}/{toId}/message")
    public String message(@PathVariable("link") String link, @PathVariable("toId") int toId, @RequestParam("message") String message, Model model) {
        boolean alreadyJoined = true;
        try {
            alreadyJoined = roomService.roomMemberExist(link);
        } catch (DuplicateRoomMemberException e) {
            logger.error("Error occurred while entering room");
        }
        if (!alreadyJoined) {
            try {
                roomService.getRoomByLink(link);
            } catch (InvalidRoomLinkException e) {
                logger.error("Error occurred while entering room");
                model.addAttribute("errorMessageTitle", "방 진입 실패");
                model.addAttribute("errorMessageContent", "유효하지 않은 주소입니다.");
                return "errorPage";
            }
            return "redirect:/home/rooms/" + link + "/join";
        }

        if (message.trim().replaceAll("\\s", "").isEmpty()) {
            model.addAttribute("errorMessageTitle", "메세지 전송 실패");
            model.addAttribute("errorMessageContent", "빈 메세지입니다.");
            return "errorPage";
        } else if (message.length() > 1024) {
            model.addAttribute("errorMessageTitle", "메세지 전송 실패");
            model.addAttribute("errorMessageContent", "너무 긴 메세지입니다.");
            return "errorPage";
        } else {
            int indexNumber = idGenerator.getIdFromSession();
            roomMessageService.createMessage(indexNumber, link, toId, message);
        }
        return "redirect:/home/rooms/" + link + "/" + toId + "/message";
    }

    @PatchMapping("/home/rooms/{link}/color")
    public String roomColor(@PathVariable("link") String link, @RequestParam("color") String color, Model model) {
        boolean alreadyJoined = true;
        try {
            alreadyJoined = roomService.roomMemberExist(link);
        } catch (DuplicateRoomMemberException e) {
            logger.error("Error occurred while entering room");
        }
        if (!alreadyJoined) {
            try {
                roomService.getRoomByLink(link);
            } catch (InvalidRoomLinkException e) {
                logger.error("Error occurred while entering room");
                model.addAttribute("errorMessageTitle", "방 진입 실패");
                model.addAttribute("errorMessageContent", "유효하지 않은 주소입니다.");
                return "errorPage";
            }
            return "redirect:/home/rooms/" + link + "/join";
        }

        if (!Pattern.matches("^#[0-9A-Fa-f]{6}$", color)) {
            model.addAttribute("errorMessageTitle", "방 색상 변경 실패");
            model.addAttribute("errorMessageContent", "적절하지 않은 색상 코드입니다.");
            return "errorPage";
        }

        roomMemberService.updateRoomMember(link, idGenerator.getIdFromSession(), null, color);
        return "redirect:/home";
    }

    @PostMapping("/home/rooms/{link}/leave")
    public ResponseEntity<?> leave(@PathVariable("link") String link) {
        boolean alreadyJoined = true;
        try {
            alreadyJoined = roomService.roomMemberExist(link);
        } catch (DuplicateRoomMemberException e) {
            logger.error("Error occurred while entering room");
        }
        if (!alreadyJoined) {
            try {
                roomService.getRoomByLink(link);
            } catch (InvalidRoomLinkException e) {
                logger.error("Error occurred while entering room");
                return ResponseEntity.badRequest().body("Failed to leave the room: Room Not Exist");
            }
            return ResponseEntity.badRequest().body("Failed to leave the room: Not Belonged");
        }

//        if (!Objects.equals(link, confirmCode)) {
//            return ResponseEntity.badRequest().body("Failed to leave the room: Incorrect Confirm Code");
//        }

        roomMemberService.deleteRoomMember(link, idGenerator.getIdFromSession());
        return ResponseEntity.ok().build();
    }
}
