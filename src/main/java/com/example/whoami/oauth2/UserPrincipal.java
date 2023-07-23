package com.example.whoami.oauth2;

import com.example.whoami.domain.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UserPrincipal implements OAuth2User {
    private int id;
    private String username;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public static UserPrincipal create(Member member, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setId(member.getIndexNumber());
        userPrincipal.setUsername(member.getMemberId());
        userPrincipal.setEmail(member.getEmail());
        userPrincipal.setPassword(member.getPassword());
        userPrincipal.setAttributes(attributes); // attributes 설정 추가

        // 사용자의 권한 정보를 설정합니다.
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        userPrincipal.setAuthorities(authorities);

        return userPrincipal;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }


    @Override
    public String getName() {
        return username;
    }
}

