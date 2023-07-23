package com.example.whoami.controller;

import com.example.whoami.dto.InquiryDTO;
import com.example.whoami.java.IdGenerator;
import com.example.whoami.service.InquiryService;
import com.example.whoami.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class InquiryController {

    private final IdGenerator idGenerator;
    private final MemberService memberService;
    private final InquiryService inquiryService;

    @Autowired
    public InquiryController(IdGenerator idGenerator, MemberService memberService, InquiryService inquiryService) {
        this.idGenerator = idGenerator;
        this.memberService = memberService;
        this.inquiryService = inquiryService;
    }

    @GetMapping("/home/talk")
    public String talk(Model model) {
        // read talk
        memberService.readAnswer(idGenerator.getIdFromSession());

        List<InquiryDTO> inquiryDTOList = inquiryService.findInquiryById(idGenerator.getIdFromSession());
        List<InquiryDTO> talkWithDates = new ArrayList<>();
        LocalDate previousDate = null;
        for (InquiryDTO inquiryDTO : inquiryDTOList) {
            LocalDate currentDate = inquiryDTO.getSendTime().toLocalDate();
            if (previousDate == null || !previousDate.equals(currentDate)) {
                InquiryDTO inquiryDTOTemp = new InquiryDTO();
                inquiryDTOTemp.setWrapper(true);
                inquiryDTOTemp.setSendTime(inquiryDTO.getSendTime());
                talkWithDates.add(inquiryDTOTemp);
                previousDate = currentDate;
            }
            inquiryDTO.setWrapper(false);
            inquiryDTO.setMessage(inquiryDTO.getMessage().replace("\r\n", "<br>"));
            talkWithDates.add(inquiryDTO);
        }
        model.addAttribute("talk", talkWithDates);
        return "etc/talk";
    }

    @PostMapping("/home/talk")
    public ResponseEntity<?> talk(@RequestParam("message") String message) {
        inquiryService.saveInquiry(idGenerator.getIdFromSession(), message, "Q");
        return ResponseEntity.ok().build();
    }
}
