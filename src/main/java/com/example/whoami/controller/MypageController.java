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
import java.util.regex.Pattern;

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
    public String email(@RequestParam("email") String email, Model model) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z0-9-.]+$";
        if (!email.isEmpty() && !Pattern.matches(regex, email)) {
            model.addAttribute("errorMessageTitle", "회원가입 실패");
            model.addAttribute("errorMessageContent", "이메일 형식이 맞지 않습니다.");
            return "errorPage";
        } else if (memberService.checkDuplicateEmail(email)) {
            model.addAttribute("errorMessageTitle", "회원가입 실패");
            model.addAttribute("errorMessageContent", "중복된 이메일입니다.");
            return "errorPage";
        }

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
    public String password(@RequestParam("currentPassword") String currentPassword, @RequestParam("password") String password, @RequestParam("passwordCheck") String passwordCheck, Model model) {
        // 현재 password 확인
        int indexNumber = idGenerator.getIdFromSession();
        if (memberService.checkCurrentPassword(indexNumber, currentPassword)) {
            if (password.length() < 10 || password.length() > 20) {
                model.addAttribute("errorMessageTitle", "회원가입 실패");
                model.addAttribute("errorMessageContent", "비밀번호의 길이는 10 ~ 20 사이여야 합니다.");
                return "errorPage";
            } else if (password.contains(" ")) {
                model.addAttribute("errorMessageTitle", "회원가입 실패");
                model.addAttribute("errorMessageContent", "비밀번호는 공백 없이 입력해주세요.");
                return "errorPage";
            } else if (!Pattern.matches("^[a-zA-Z0-9!@#$%^&*]+$", password)) {
                model.addAttribute("errorMessageTitle", "회원가입 실패");
                model.addAttribute("errorMessageContent", "비밀번호는 숫자와 영문, 특수문자로만 구성되어야 합니다.");
                return "errorPage";
            } else if (!Pattern.matches("(?=.*\\d)(?=.*[a-zA-Z]).*", password)) {
                model.addAttribute("errorMessageTitle", "회원가입 실패");
                model.addAttribute("errorMessageContent", "비밀번호는 숫자와 영문을 포함해야 합니다.");
                return "errorPage";
            }
            if (!Objects.equals(password, passwordCheck)) {
                model.addAttribute("errorMessageTitle", "회원가입 실패");
                model.addAttribute("errorMessageContent", "비밀번호가 일치하지 않습니다.");
                return "errorPage";
            }

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
