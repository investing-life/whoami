package com.example.whoami.service;

import com.example.whoami.dto.InquiryDTO;

import java.util.List;

public interface InquiryService {
    public List<InquiryDTO> findInquiryById(int memberId);
    public void saveInquiry(int memberId, String message, String messageType);
}
