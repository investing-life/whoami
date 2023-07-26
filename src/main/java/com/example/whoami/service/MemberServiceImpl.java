package com.example.whoami.service;

import com.example.whoami.domain.Member;
import com.example.whoami.domain.RoomMember;
import com.example.whoami.dto.MemberDTO;
import com.example.whoami.dto.MemberInfoDTO;
import com.example.whoami.repository.MemberRepository;
import com.example.whoami.repository.RoomMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository, RoomMemberRepository roomMemberRepository, PasswordEncoder passwordEncoder, OAuth2AuthorizedClientService authorizedClientService) {
        this.memberRepository = memberRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public String createMember(MemberDTO dto) {
        String encryptedPassword = passwordEncoder.encode(dto.getPassword());
        String email = null;
        if (!dto.getEmail().equals("")) {
            email = dto.getEmail();
        }
        Member member = new Member(dto.getId(), encryptedPassword, email);
        member.setLastAccessTime(LocalDateTime.now());
        member.setNewNotice(true);
        memberRepository.save(member);
        return dto.getId();
    }

    @Override
    public boolean checkDuplicateID(String id) {
        Optional<Member> existingMember = memberRepository.findByMemberId(id);
        return existingMember.isPresent();
    }

    @Override
    public boolean checkDuplicateEmail(String email) {
        Optional<Member> existingMember = memberRepository.findByEmailAndDeleted(email, false);
        return existingMember.isPresent();
    }

    @Override
    public MemberInfoDTO getMemberInfo(Integer indexNumber) {
        Optional<Member> member = memberRepository.findById(indexNumber);
        MemberInfoDTO memberInfoDTO = new MemberInfoDTO();
        if (member.isPresent()) {
            memberInfoDTO.setId(member.get().getMemberId());
            memberInfoDTO.setEmail(member.get().getEmail());
            memberInfoDTO.setOauth(member.get().getOauth());
            memberInfoDTO.setJoinDate(member.get().getJoinDate());
            memberInfoDTO.setAdmin(member.get().isAdmin());
            if (member.get().getOpenness() != 0 &&
                    member.get().getConscientiousness() != 0 &&
                    member.get().getExtraversion() != 0 &&
                    member.get().getAgreeableness() != 0 &&
                    member.get().getNeuroticism() != 0) {
                memberInfoDTO.setTestDone(true);
            } else {
                memberInfoDTO.setTestDone(false);
            }
            memberInfoDTO.setOpenness(member.get().getOpenness());
            memberInfoDTO.setConscientiousness(member.get().getConscientiousness());
            memberInfoDTO.setExtraversion(member.get().getExtraversion());
            memberInfoDTO.setAgreeableness(member.get().getAgreeableness());
            memberInfoDTO.setNeuroticism(member.get().getNeuroticism());

        } else {
            // maybe user withdrew while loading
        }
        return memberInfoDTO;
    }

    @Override
    public void updateMember(MemberDTO dto) {
        Optional<Member> memberOptional = memberRepository.findById(dto.getIndexNumber());
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            if (dto.getPassword() != null) {
                String encryptedPassword = passwordEncoder.encode(dto.getPassword());
                member.setPassword(encryptedPassword);
            }
            if (dto.getEmail() != null) {
                if (dto.getEmail().equals("")) {
                    member.setEmail(null);
                }
                member.setEmail(dto.getEmail());
            }
            if (dto.getTestResult() != null) {
                member.setSex(dto.getSex());
                member.setAge(dto.getAge());
                member.setOpenness(dto.getOpenness());
                member.setConscientiousness(dto.getConscientiousness());
                member.setExtraversion(dto.getExtraversion());
                member.setAgreeableness(dto.getAgreeableness());
                member.setNeuroticism(dto.getNeuroticism());
                member.setTestResult(dto.getTestResult());
            }
            memberRepository.save(member);
        } else {
            // maybe user withdrew while loading
        }
    }

    @Override
    public boolean checkCurrentPassword(int indexNumber, String password) {
        Optional<Member> memberOptional = memberRepository.findById(indexNumber);
        if (memberOptional.isPresent()) {
            return passwordEncoder.matches(password, memberOptional.get().getPassword());
        } else {
            // maybe user withdrew while loading
            return false;
        }
    }

    @Override
    public void deleteMember(Integer indexNumber) {
        Optional<Member> memberOptional = memberRepository.findById(indexNumber);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();

            // 카카오 계정일 때는 id 바꾸기
            if (member.getOauth() != null) {
                int i = 0;
                while (true) {
                    String newMemberId = member.getMemberId() + '-' + i;
                    if (memberRepository.findByMemberIdAndDeleted(newMemberId, true).isEmpty()) {
                        member.setMemberId(newMemberId);
                        break;
                    }
                    i++;
                }
            }
            member.setPassword("invalid");
            member.setEmail(null);
            member.setSex(null);
            member.setAge(null);
            member.setDeleted(true);
            member.setOpenness(0);
            member.setConscientiousness(0);
            member.setExtraversion(0);
            member.setAgreeableness(0);
            member.setNeuroticism(0);
            member.setTestResult(null);

            Optional<List<RoomMember>> roomMemberListOptional = roomMemberRepository.findAllByMember_IndexNumberAndDeleted(indexNumber, false);
            if (roomMemberListOptional.isPresent()) {
                for (RoomMember roomMember : roomMemberListOptional.get()) {
                    roomMember.setDeleted(true);
                    roomMemberRepository.save(roomMember);
                }
            }

            memberRepository.save(member);

            // 카카오 oauth 계정이었으면 카카오 연결 끊기 API 사용
            if (member.getOauth() != null) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof OAuth2AuthenticationToken oauth2Authentication) {
                    OAuth2AuthorizedClient authorizedClient =
                            authorizedClientService.loadAuthorizedClient(
                                    oauth2Authentication.getAuthorizedClientRegistrationId(),
                                    oauth2Authentication.getName()
                            );

                    if (authorizedClient != null) {
                        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
                        String accessTokenValue = accessToken.getTokenValue();

                        // 액세스 토큰 사용
                        RestTemplate restTemplate = new RestTemplate();

                        // 요청 헤더 설정
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.set("Authorization", "Bearer " + accessTokenValue);

                        // 요청 본문 설정 (JSON 형식)
                        String requestBody = "{}";

                        // 요청 엔티티 생성
                        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

                        // POST 요청 보내기
                        String url = "https://kapi.kakao.com/v1/user/unlink";
                        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

                    }
                }

            }
        }
    }

    @Override
    public void updateLastAccessTime(int indexNumber, LocalDateTime lastAccessTime) {
        Optional<Member> memberOptional = memberRepository.findById(indexNumber);
        Member member = memberOptional.get();
        member.setLastAccessTime(lastAccessTime);
        memberRepository.save(member);
    }

    @Override
    public void readQuestion(int indexNumber) {
        Optional<Member> memberOptional = memberRepository.findById(indexNumber);
        Member member = memberOptional.get();
        member.setNewQuestion(false);
        memberRepository.save(member);
    }

    @Override
    public void readAnswer(int indexNumber) {
        Optional<Member> memberOptional = memberRepository.findById(indexNumber);
        Member member = memberOptional.get();
        member.setNewAnswer(false);
        memberRepository.save(member);
    }

    @Override
    public void readNotice(int indexNumber) {
        Optional<Member> memberOptional = memberRepository.findById(indexNumber);
        Member member = memberOptional.get();
        member.setNewNotice(false);
        memberRepository.save(member);
    }

    @Override
    public Map<String, Boolean> getSignals(int indexNumber) {
        Optional<Member> memberOptional = memberRepository.findById(indexNumber);
        Member member = memberOptional.get();
        Map<String, Boolean> signals = new HashMap<>();
        signals.put("newAnswer", member.isNewAnswer());
        signals.put("newNotice", member.isNewNotice());
        return signals;
    }

    @Override
    public String getLocalMemberIdFromEmail(String email) {
        Optional<Member> memberOptional = memberRepository.findByEmailAndDeleted(email, false);
        if (memberOptional.isEmpty()) {
            return null;
        } else if (memberOptional.get().getOauth() != null) {
            return null;
        } else {
            return memberOptional.get().getMemberId();
        }
    }

    @Override
    public String resetPassword(String memberId, String email) {
        Optional<Member> memberOptional = memberRepository.findByMemberIdAndDeleted(memberId, false);
        if (memberOptional.isEmpty()) {
            return null;
        } else if (memberOptional.get().getEmail() == null || !memberOptional.get().getEmail().equals(email)) {
            return null;
        } else {
            Member member = memberOptional.get();

            // 임시 비밀번호
            String UPPER_CASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String LOWER_CASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
            String NUMERIC_CHARS = "0123456789";
            String SPECIAL_CHARS = "!@#$%^&*";

            SecureRandom random = new SecureRandom();
            StringBuilder password = new StringBuilder();

            // Ensure at least 2 character types are used for password length >= 10
            boolean useUpperCase = false;
            boolean useLowerCase = false;
            boolean useNumeric = false;
            boolean useSpecialChars = false;

            // Generate password with the required character types
            while (true) {
                password = new StringBuilder();
                useUpperCase = false;
                useLowerCase = false;
                useNumeric =  false;
                useSpecialChars = false;
                while (password.length() < 10) {
                    int charType = random.nextInt(4); // 0: Upper Case, 1: Lower Case, 2: Numeric, 3: Special Chars

                    switch (charType) {
                        case 0:
                            password.append(UPPER_CASE_CHARS.charAt(random.nextInt(UPPER_CASE_CHARS.length())));
                            useUpperCase = true;
                            break;
                        case 1:
                            password.append(LOWER_CASE_CHARS.charAt(random.nextInt(LOWER_CASE_CHARS.length())));
                            useLowerCase = true;
                            break;
                        case 2:
                            password.append(NUMERIC_CHARS.charAt(random.nextInt(NUMERIC_CHARS.length())));
                            useNumeric = true;
                            break;
                        case 3:
                            password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));
                            useSpecialChars = true;
                            break;
                    }
                }
                if ((useUpperCase ? 1 : 0) + (useLowerCase ? 1 : 0) + (useNumeric ? 1 : 0) + (useSpecialChars ? 1 : 0) >= 3) {
                    break;
                }
            }

            member.setPassword(passwordEncoder.encode(password));
            memberRepository.save(member);
            return password.toString();
        }
    }
}
