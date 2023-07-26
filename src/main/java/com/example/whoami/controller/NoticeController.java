package com.example.whoami.controller;

import com.example.whoami.dto.NoticeDTO;
import com.example.whoami.java.IdGenerator;
import com.example.whoami.service.MemberService;
import com.example.whoami.service.NoticeService;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class NoticeController {

    private final MemberService memberService;
    private final NoticeService noticeService;
    private final IdGenerator idGenerator;

    @Autowired
    public NoticeController(MemberService memberService, NoticeService noticeService, IdGenerator idGenerator) {
        this.memberService = memberService;
        this.noticeService = noticeService;
        this.idGenerator = idGenerator;
    }

    @GetMapping("/home/notice")
    public String notice(Model model) {
        // read notice
        memberService.readNotice(idGenerator.getIdFromSession());

        List<NoticeDTO> noticeDTOList = noticeService.getAllNotice();
        for (NoticeDTO noticeDTO : noticeDTOList) {
            noticeDTO.setContent(noticeDTO.getContent().replace("\r\n", "<br>"));
        }
        Collections.reverse(noticeDTOList);
        List<List<NoticeDTO>> chunkedNotices = ListUtils.partition(noticeDTOList, 5);
        model.addAttribute("chunkedNotices", chunkedNotices);
        return "etc/notice";
    }
}
