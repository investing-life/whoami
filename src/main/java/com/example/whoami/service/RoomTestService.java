package com.example.whoami.service;

import com.example.whoami.dto.TestDTO;

import java.util.List;
import java.util.Map;

public interface RoomTestService {
    public void createTest(TestDTO testDTO);
    public TestDTO findReceivedTestScoresById(int id, String roomLink);
    public List<Integer> getTestList(int id, String roomLink);
}
