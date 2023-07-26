package com.example.whoami.controller;

import com.example.whoami.config.EmailConfig;
import com.example.whoami.dto.MemberDTO;
import com.example.whoami.form.SignUpForm;
import com.example.whoami.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;

@Controller
public class MemberController {

    private final MemberService memberService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final EmailConfig emailConfig;

    @Autowired
    public MemberController(MemberService memberService, OAuth2AuthorizedClientService authorizedClientService, EmailConfig emailConfig) {
        this.memberService = memberService;
        this.authorizedClientService = authorizedClientService;
        this.emailConfig = emailConfig;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 현재 인증된 사용자의 Authentication 객체 가져오기

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            // Principal 가져오기
            Object principal = auth.getPrincipal();

            // OAuth 계정인지 확인
            boolean isOAuthAccount = false;
            if (principal instanceof OAuth2User) {
                // OAuth 계정일 경우
                isOAuthAccount = true;
            }

            // oauth 로그아웃
            if (isOAuthAccount) {

               // 카카오 로그아웃 API 엔드포인트 URL
                String logoutUrl = "https://kapi.kakao.com/v1/user/logout";

                // 카카오 액세스 토큰 (로그아웃할 사용자의 토큰)
                String accessTokenValue = "";
                if (auth instanceof OAuth2AuthenticationToken oauth2Authentication) {
                    OAuth2AuthorizedClient authorizedClient =
                            authorizedClientService.loadAuthorizedClient(
                                    oauth2Authentication.getAuthorizedClientRegistrationId(),
                                    oauth2Authentication.getName()
                            );

                    if (authorizedClient != null) {
                        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
                        accessTokenValue = accessToken.getTokenValue();
                    }
                }

                // HTTP POST 요청 보내기
                URL url = new URL(logoutUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + accessTokenValue);

                // 응답 읽기
                int responseCode = conn.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response_ = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response_.append(inputLine);
                }
                in.close();

                // 응답 결과 처리
                if (responseCode == HttpURLConnection.HTTP_OK) {
                } else {
                }
            }

            // 로그아웃 처리
            new SecurityContextLogoutHandler().logout(request, response, auth);

            // rememberMe 쿠키 삭제
            Cookie rememberMeCookie = new Cookie("rememberMeCookie", null);
            rememberMeCookie.setMaxAge(0);
            rememberMeCookie.setPath("/");
            response.addCookie(rememberMeCookie);
        }
        // 로그아웃 후 리다이렉트할 페이지로 이동
        return "redirect:/";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("SignUpForm", new SignUpForm());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(SignUpForm signUpForm, Model model) {
        // 유효성 검사
        String id = signUpForm.getId();
        String password = signUpForm.getPassword();
        String passwordCheck = signUpForm.getPasswordCheck();
        String email = signUpForm.getEmail();
        if (id.length() < 4 || id.length() > 20) {
            model.addAttribute("errorMessageTitle", "회원가입 실패");
            model.addAttribute("errorMessageContent", "아이디의 길이는 4 ~ 20 사이여야 합니다.");
            return "errorPage";
        } else if (!Pattern.matches("^[a-zA-Z0-9]+$", id)) {
            model.addAttribute("errorMessageTitle", "회원가입 실패");
            model.addAttribute("errorMessageContent", "아이디는 숫자와 영문으로만 구성되어야 합니다.");
            return "errorPage";
        } else if (memberService.checkDuplicateID(id)) {
            model.addAttribute("errorMessageTitle", "회원가입 실패");
            model.addAttribute("errorMessageContent", "중복된 아이디입니다.");
            return "errorPage";
        }
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

        MemberDTO dto = new MemberDTO(signUpForm.getId(), signUpForm.getPassword(), signUpForm.getEmail());
        memberService.createMember(dto);

        return "redirect:/login";
    }

    @PostMapping("/checkDuplicateID")
    @ResponseBody
    public Map<String, Boolean> checkDuplicateID(@RequestParam("ID") String id) {
        boolean isDuplicate = memberService.checkDuplicateID(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("duplicate", isDuplicate);
        return response;
    }

    @PostMapping("/checkDuplicateEmail")
    @ResponseBody
    public Map<String, Boolean> checkDuplicateEmail(@RequestParam("email") String email) {
        boolean isDuplicate = memberService.checkDuplicateEmail(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("duplicate", isDuplicate);
        return response;
    }

    @GetMapping("/find-id")
    public String findId() {
        return "find-id";
    }

    @PostMapping("/find-id")
    @ResponseBody
    public ResponseEntity<String> findId(@RequestParam("email") String email) {
        String memberId = memberService.getLocalMemberIdFromEmail(email);

        // SMTP 서버 및 인증 정보 설정
        String host = emailConfig.getEmailHost();
        String username = emailConfig.getEmailUsername();
        String password = emailConfig.getEmailPassword();

        // 속성 설정
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", emailConfig.getEmailPort());

        // 세션 생성
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // 메시지 생성
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "WhoAmI")); // 보낸 이 주소와 이름 설정
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("[WhoAmI] 아이디 검색 결과");
            if (memberId == null) {
                String htmlContent = "<h1>해당 이메일에 연동된 Who Am I 계정이 존재하지 않습니다.</h1>" +
                        "<p>안녕하세요. 서비스 Who Am I 입니다.<br>아이디 찾기를 요청하지 않으셨다면 이 메일을 무시해주세요. 누군가 귀하의 메일 주소를 잘못 입력한 것일 수 있습니다.</p>";
                message.setContent(htmlContent, "text/html; charset=utf-8");
            } else {
                String htmlContent = "<h1>회원님의 아이디는 [<span style='color:#006400'>" + memberId + "</span>]입니다.</h1>" +
                        "<p>안녕하세요. 서비스 Who Am I 입니다.<br><span style='color:#006400'>" + email + "</span>님의 아이디는 [<span style='color:#006400'>" + memberId + "</span>] 입니다.</p>" +
                        "<p>Who Am I 를 이용해 주셔서 감사합니다.<br>더욱 편리한 서비스를 위해 최선을 다하겠습니다.</p>";
                message.setContent(htmlContent, "text/html; charset=utf-8");
            }

            // 이메일 전송
            Transport.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String response = "메일이 발송되었으니 확인 부탁드립니다.";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/reset-pwd")
    public String resetPwd() {
        return "reset-pwd";
    }

    @PostMapping("/reset-pwd")
    @ResponseBody
    public ResponseEntity<String> findPwd(@RequestParam("id") String id, @RequestParam("email") String email) throws JsonProcessingException {
        // SMTP 서버 및 인증 정보 설정
        String host = emailConfig.getEmailHost();
        String username = emailConfig.getEmailUsername();
        String password = emailConfig.getEmailPassword();

        // 속성 설정
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", emailConfig.getEmailPort());

        // 세션 생성
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // 메시지 생성
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "WhoAmI")); // 보낸 이 주소와 이름 설정
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("[WhoAmI] 비밀번호 초기화 성공");

            String newPassword = memberService.resetPassword(id, email);
            if (newPassword == null) {
                Map<String, String> response = new HashMap<>();
                response.put("succeed", "false");
                response.put("message", "일치하는 계정이 존재하지 않습니다.");
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(response);
                return new ResponseEntity<>(json, HttpStatus.OK);
            } else {
                String htmlContent = "<h1>회원님의 임시 비밀번호는 [<span style='color:#006400'>" + newPassword + "</span>]입니다.</h1>" +
                        "<p>안녕하세요. 서비스 Who Am I 입니다.<br><span style='color:#006400'>" + id + "</span>님의 임시 비밀번호는 [<span style='color:#006400'>" + newPassword + "</span>] 입니다.</p>" +
                        "<p>Who Am I 를 이용해 주셔서 감사합니다.<br>더욱 편리한 서비스를 위해 최선을 다하겠습니다.</p>";
                message.setContent(htmlContent, "text/html; charset=utf-8");
            }

            // 이메일 전송
            Transport.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Map<String, String> response = new HashMap<>();
        response.put("succeed", "true");
        response.put("message", "메일이 발송되었으니 확인 부탁드립니다.");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(response);
        return new ResponseEntity<>(json, HttpStatus.OK);
    }
}
