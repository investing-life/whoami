package com.example.whoami.controller;

import com.example.whoami.domain.Member;
import com.example.whoami.dto.MemberDTO;
import com.example.whoami.dto.MemberInfoDTO;
import com.example.whoami.java.IdGenerator;
import com.example.whoami.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Controller
public class MypageController {

    private final MemberService memberService;
    private final IdGenerator idGenerator;

    @Autowired
    public MypageController(MemberService memberService, IdGenerator idGenerator) {
        this.memberService = memberService;
        this.idGenerator = idGenerator;
    }

    @GetMapping("/home/mypage")
    public String mypage(Model model) {
        int indexNumber = idGenerator.getIdFromSession();
        model.addAttribute("userInfo", memberService.getMemberInfo(indexNumber));
        return "etc/my-page";
    }

    @GetMapping("/home/mypage/email")
    public String email() {
        return "etc/email";
    }

    @PatchMapping("/home/mypage/email")
    public String email(@RequestParam("email") String email) {
        int indexNumber = idGenerator.getIdFromSession();
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setIndexNumber(indexNumber);
        memberDTO.setEmail(email);
        memberService.updateMember(memberDTO);
        return "redirect:/home/mypage";
    }

    @GetMapping("/home/mypage/password")
    public String password(Model model, @RequestParam(name = "error", required = false) String error) {
        if (error != null) {
            model.addAttribute("error", true);
        } else {
            model.addAttribute("error", false);
        }
        return "etc/password";
    }

    @PatchMapping("/home/mypage/password")
    public String password(@RequestParam("currentPassword") String currentPassword, @RequestParam("password") String password) {
        // 현재 password 확인
        int indexNumber = idGenerator.getIdFromSession();
        if (memberService.checkCurrentPassword(indexNumber, currentPassword)) {
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setIndexNumber(indexNumber);
            memberDTO.setPassword(password);
            memberService.updateMember(memberDTO);
            return "redirect:/home/mypage";
        } else {
            return "redirect:/home/mypage/password?error";
        }
    }

    @GetMapping("/home/mypage/withdrawal")
    public String withdrawal(Model model, @RequestParam(name = "error", required = false) String error) {
        if (error != null) {
            model.addAttribute("error", true);
        } else {
            model.addAttribute("error", false);
        }
        int indexNumber = idGenerator.getIdFromSession();
        MemberInfoDTO memberInfoDTO = memberService.getMemberInfo(indexNumber);
        model.addAttribute("oauth", memberInfoDTO.getOauth());
        if (memberInfoDTO.getOauth() == null) {
            model.addAttribute("ID", memberInfoDTO.getId());
        }
        return "etc/withdrawal";
    }

    @PatchMapping("/home/mypage/withdrawal")
    public String withdrawal(@RequestParam(name = "currentPassword", required = false) String currentPassword, HttpServletRequest request) {
        // 현재 password 확인
        int indexNumber = idGenerator.getIdFromSession();
        if (memberService.getMemberInfo(indexNumber).getOauth() != null || memberService.checkCurrentPassword(indexNumber, currentPassword)) {
            memberService.deleteMember(indexNumber);
            HttpSession session = request.getSession();
            session.invalidate();
            return "redirect:/";
        } else {
            return "redirect:/home/mypage/withdrawal?error";
        }
    }
}
