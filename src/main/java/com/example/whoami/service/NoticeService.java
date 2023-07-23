package com.example.whoami.service;

import com.example.whoami.dto.NoticeDTO;

import java.util.List;

public interface NoticeService {
    public List<NoticeDTO> getAllNotice();

    public void createNotice(String title, String content);

    public void updateNotice(int indexNumber, String title, String content);

    public void deleteNotice(int indexNumber);
}
