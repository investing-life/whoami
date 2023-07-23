package com.example.whoami.java;

import com.example.whoami.domain.Member;
import com.example.whoami.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> memberOptional = memberRepository.findByMemberIdAndDeleted(username, false);

        if (memberOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        Member member = memberOptional.get();
        // 사용자의 "admin" 컬럼 값에 따라 Role 설정
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (member.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // UserDetails 객체 생성 및 반환
        return new org.springframework.security.core.userdetails.User(member.getMemberId(), member.getPassword(), authorities);
    }
}

