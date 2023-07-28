package com.example.whoami.service;

import com.example.whoami.domain.Member;
import com.example.whoami.dto.MemberDTO;
import com.example.whoami.dto.MemberInfoDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MemberService {

    public String createMember(MemberDTO dto);

    public boolean checkDuplicateID(String id);

    public boolean checkDuplicateEmail(String email);

    public MemberInfoDTO getMemberInfo(Integer indexNumber);

    public MemberInfoDTO getMemberInfoAndUpdateLastAccessTime(Integer indexNumber);

    public void updateMember(MemberDTO dto);

    public boolean checkCurrentPassword(int indexNumber, String password);

    public void deleteMember(Integer indexNumber);

    public void updateLastAccessTime(int indexNumber, LocalDateTime lastAccessTime);

    public void readQuestion(int indexNumber);

    public void readAnswer(int indexNumber);

    public void readNotice(int indexNumber);

    public Map<String, Boolean> getSignals(int indexNumber);

    public String getLocalMemberIdFromEmail(String email);

    public String resetPassword(String memberId, String email);
}
