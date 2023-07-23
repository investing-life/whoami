package com.example.whoami.service;

import com.example.whoami.domain.Inquiry;
import com.example.whoami.domain.Member;
import com.example.whoami.dto.InquiryDTO;
import com.example.whoami.repository.InquiryRepository;
import com.example.whoami.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public InquiryServiceImpl(InquiryRepository inquiryRepository, MemberRepository memberRepository) {
        this.inquiryRepository = inquiryRepository;
        this.memberRepository = memberRepository;
    }

    public List<InquiryDTO> findInquiryById(int memberId) {
        Optional<List<Inquiry>> optionalInquiryList = inquiryRepository.findAllByMember_IndexNumber(memberId);
        List<InquiryDTO> inquiryDTOList = new ArrayList<>();
        if (optionalInquiryList.isPresent()) {
            for (Inquiry inquiry : optionalInquiryList.get()) {
                InquiryDTO inquiryDTO = new InquiryDTO();
                inquiryDTO.setMessage(inquiry.getMessage());
                inquiryDTO.setMessageType(inquiry.getMessageType());
                inquiryDTO.setSendTime(inquiry.getSendTime());
                inquiryDTOList.add(inquiryDTO);
            }
        }
        return inquiryDTOList;
    }

    public void saveInquiry(int memberId, String message, String messageType) {
        Inquiry inquiry = new Inquiry();
        inquiry.setMemberId(memberId);
        inquiry.setMessage(message);
        inquiry.setMessageType(messageType);
        inquiryRepository.save(inquiry);

        Optional<Member> memberOptional = memberRepository.findById(memberId);
        Member member = memberOptional.get();
        if (Objects.equals(messageType, "Q")) {
            member.setNewQuestion(true);
        } else if (Objects.equals(messageType, "A")) {
            member.setNewAnswer(true);
        }
        memberRepository.save(member);
    }
}
