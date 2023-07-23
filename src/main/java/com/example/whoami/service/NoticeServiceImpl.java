package com.example.whoami.service;

import com.example.whoami.domain.Notice;
import com.example.whoami.dto.NoticeDTO;
import com.example.whoami.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {
    private final NoticeRepository noticeRepository;

    @Autowired
    public NoticeServiceImpl(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @Override
    public List<NoticeDTO> getAllNotice() {
        Sort inquirySort = Sort.by(Sort.Direction.ASC, "regDate");
        List<NoticeDTO> noticeDTOList = new ArrayList<>();
        List<Notice> noticeList = noticeRepository.findAll();
        int i = 1;
        for (Notice notice : noticeList) {
            NoticeDTO noticeDTO = new NoticeDTO();
            noticeDTO.setIndexNumber(notice.getIndexNumber());
            noticeDTO.setSequenceNumber(i);
            noticeDTO.setTitle(notice.getTitle());
            noticeDTO.setContent(notice.getContent());
            noticeDTO.setRegDate(notice.getRegDate());
            noticeDTOList.add(noticeDTO);
            i++;
        }
        return noticeDTOList;
    }

    public void createNotice(String title, String content) {
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setRegDate(LocalDateTime.now());
        noticeRepository.save(notice);
    }

    public void updateNotice(int indexNumber, String title, String content) {
        Notice notice = noticeRepository.findById(indexNumber).get();
        notice.setTitle(title);
        notice.setContent(content);
        noticeRepository.save(notice);
    }

    public void deleteNotice(int indexNumber) {
        noticeRepository.deleteById(indexNumber);
    }
}
