package com.example.whoami.java;

import com.example.whoami.domain.Member;
import com.example.whoami.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class IdGenerator {

    private final MemberRepository memberRepository;

    @Autowired
    public IdGenerator(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public int getIdFromSession() {
        // OAuth2 인증 정보를 사용하여 세션에서 인증된 사용자 정보를 가져옴
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String memberId = null;
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof OAuth2User) {
                // OAuth2User 객체를 사용하여 멤버 ID 가져옴
                // kakao 경우는 getAttribute("id")이지만 provider에 따라 다를 수 있음
                memberId = ((OAuth2User) principal).getName();
            } else if (principal instanceof UserDetails) {
                // UserDetails 객체를 사용하여 멤버 ID 가져옴
                memberId = ((UserDetails) principal).getUsername();
            } else if (principal instanceof Member) {
                // CustomUserDetails 클래스 등 사용자 정의 객체를 사용하여 멤버 ID 가져옴
                memberId = ((Member) principal).getMemberId();
            }
        }
        int id = 0;
        Optional<Member> memberOptional = memberRepository.findByMemberIdAndDeleted(memberId, false);
        if (memberOptional.isPresent()) {
            id = memberOptional.get().getIndexNumber();
        }
        return id;
    }

}
