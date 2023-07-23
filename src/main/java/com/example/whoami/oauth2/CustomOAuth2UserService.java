package com.example.whoami.oauth2;

import com.example.whoami.domain.Member;
import com.example.whoami.repository.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;


@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    public CustomOAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(oAuth2UserRequest);

        AuthProvider authProvider = AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase());
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(authProvider, oAuth2User.getAttributes());

        Optional<Member> memberOptional = memberRepository.findByMemberIdAndDeleted("kakao-" + Base64.getEncoder().encodeToString(String.valueOf(oAuth2UserInfo.getId()).getBytes()), false);
        if (memberOptional.isPresent()) {
            // 가입된 경우
            return UserPrincipal.create(memberOptional.get(), oAuth2UserInfo.getAttributes());
        } else {
            // 가입되지 않은 경우
            Member member = new Member("kakao-" + Base64.getEncoder().encodeToString(String.valueOf(oAuth2UserInfo.getId()).getBytes()), "kakao", null);
            member.setLastAccessTime(LocalDateTime.now());
            member.setOauth("kakao");
            // gender, age_range 설정
//            String gender = (String) oAuth2UserInfo.getAttributes().get("gender");
//            String ageRange = (String) oAuth2UserInfo.getAttributes().get("age_range");
//
//            if (gender != null) {
//                if (gender.equals("female")) {
//                    member.setSex("여");
//                } else if (gender.equals("male")) {
//                    member.setSex("남");
//                }
//            }
//
//            if (ageRange != null) {
//                switch (ageRange) {
//                    case "1~9", "10~14", "15~19" -> member.setAge("10대 이하");
//                    case "20~29" -> member.setAge("20대");
//                    case "30~39" -> member.setAge("30대");
//                    case "40~49" -> member.setAge("40대");
//                    case "50~59" -> member.setAge("50대");
//                    case "60~69", "70~79", "80~89", "90~" -> member.setAge("60대 이상");
//                }
//            }

            memberRepository.save(member);
            return UserPrincipal.create(member, oAuth2UserInfo.getAttributes());
        }
    }
}

