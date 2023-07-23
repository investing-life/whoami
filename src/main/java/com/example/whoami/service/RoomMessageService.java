package com.example.whoami.service;

import com.example.whoami.dto.MessageDTO;

import java.util.List;

public interface RoomMessageService {
    public List<MessageDTO> findReceivedMessagesById(int id, String roomLink);

    public List<MessageDTO> findSentMessagesById(int id, int toId, String roomLink);

    public void createMessage(int id, String roomLink, int toId, String message);
}
