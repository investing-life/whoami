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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public String newRoom(@RequestParam String name, @RequestParam String nickname, @RequestParam String color) {
        String link = homeService.createRoom(name, nickname, color);
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
