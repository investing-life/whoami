package com.example.whoami.controller;

import com.example.whoami.dto.RoomInfoDTO;
import com.example.whoami.java.IdGenerator;
import com.example.whoami.service.HomeService;
import com.example.whoami.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Controller
public class HomeController {

    private final MemberService memberService;
    private final HomeService homeService;
    private final IdGenerator idGenerator;

    @Autowired
    public HomeController(MemberService memberService, HomeService homeService, IdGenerator idGenerator) {
        this.memberService = memberService;
        this.homeService = homeService;
        this.idGenerator = idGenerator;
    }


    @GetMapping("/home")
    public String home(Model model) {
        int indexNumber = idGenerator.getIdFromSession();

        // accessed
        memberService.updateLastAccessTime(indexNumber, LocalDateTime.now());

        List<RoomInfoDTO> dtoList = homeService.getRoomByMemberId(indexNumber);
        model.addAttribute("rooms", dtoList);

        Map<String, Boolean> signals = memberService.getSignals(indexNumber);
        model.addAttribute("newAnswer", signals.get("newAnswer"));
        model.addAttribute("newNotice", signals.get("newNotice"));

        return "home";
    }

    @GetMapping("/home/rooms/new")
    public String newRoom() {
        return "rooms/newroom";
    }

    @PostMapping("/home/rooms/new")
    public String newRoom(@RequestParam String name, @RequestParam String nickname, @RequestParam String color, Model model) {
        // 유효성 검사
        if (name.length() < 1 || name.length() > 15) {
            model.addAttribute("errorMessageTitle", "방 생성 실패");
            model.addAttribute("errorMessageContent", "방 이름의 길이는 1 ~ 15 사이여야 합니다.");
            return "errorPage";
        } else if (!Objects.equals(name.trim(), name)) {
            model.addAttribute("errorMessageTitle", "방 생성 실패");
            model.addAttribute("errorMessageContent", "방 이름이 공백으로 시작하거나 끝나면 안 됩니다.");
            return "errorPage";
        } else if (homeService.checkDuplicateRoomName(name)) {
            model.addAttribute("errorMessageTitle", "방 생성 실패");
            model.addAttribute("errorMessageContent", "중복된 방 이름입니다.");
            return "errorPage";
        }
        if (nickname.length() < 1 || nickname.length() > 15) {
            model.addAttribute("errorMessageTitle", "방 생성 실패");
            model.addAttribute("errorMessageContent", "이름의 길이는 1 ~ 15 사이여야 합니다.");
            return "errorPage";
        } else if (!Objects.equals(nickname.trim(), nickname)) {
            model.addAttribute("errorMessageTitle", "방 생성 실패");
            model.addAttribute("errorMessageContent", "이름이 공백으로 시작하거나 끝나면 안 됩니다.");
            return "errorPage";
        }
        if (!Pattern.matches("^#[0-9A-Fa-f]{6}$", color)) {
            model.addAttribute("errorMessageTitle", "방 생성 실패");
            model.addAttribute("errorMessageContent", "적절하지 않은 색상 코드입니다.");
            return "errorPage";
        }

        String link = homeService.createRoom(name, nickname, color);
        if (link == null) {
            model.addAttribute("errorMessageTitle", "방 생성 실패");
            model.addAttribute("errorMessageContent", "같은 이름의 방이 있습니다.");
            return "errorPage";
        }
        return "redirect:/home/rooms/new/" + link;
    }

    @PostMapping("/checkDuplicateRoomName")
    @ResponseBody
    public Map<String, Boolean> checkDuplicateRoomName(@RequestParam("name") String name) {
        boolean isDuplicate = homeService.checkDuplicateRoomName(name);
        Map<String, Boolean> response = new HashMap<>();
        response.put("duplicate", isDuplicate);
        return response;
    }

    @GetMapping("/home/rooms/new/{link}")
    public String newRoomId(Model model, @PathVariable("link") String link) {
        model.addAttribute("link", link);
        return "rooms/newroom-link";
    }

}
