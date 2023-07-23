package com.example.whoami.java;

import com.example.whoami.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private Member member;
//    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자의 권한 정보를 반환합니다.
        // 예: user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
        // 위의 예시는 User 엔티티에 roles라는 필드가 있고, Role 엔티티의 name 필드로 권한을 표현하는 경우입니다.
        return null;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getMemberId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부를 반환합니다.
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠김 여부를 반환합니다.
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명(비밀번호) 만료 여부를 반환합니다.
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부를 반환합니다.
    }
}