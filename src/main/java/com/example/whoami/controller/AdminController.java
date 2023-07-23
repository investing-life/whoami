package com.example.whoami.controller;

import com.example.whoami.domain.History;
import com.example.whoami.domain.Inquiry;
import com.example.whoami.domain.Member;
import com.example.whoami.dto.HistoryDTO;
import com.example.whoami.dto.InquiryDTO;
import com.example.whoami.dto.NoticeDTO;
import com.example.whoami.repository.HistoryRepository;
import com.example.whoami.repository.InquiryRepository;
import com.example.whoami.repository.MemberRepository;
import com.example.whoami.service.InquiryService;
import com.example.whoami.service.MemberService;
import com.example.whoami.service.NoticeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class AdminController {

    private final MemberService memberService;
    private final InquiryService inquiryService;
    private final NoticeService noticeService;

    private final MemberRepository memberRepository;
    private final HistoryRepository historyRepository;
    private final InquiryRepository inquiryRepository;

    @Autowired
    public AdminController(MemberService memberService, InquiryService inquiryService, NoticeService noticeService, MemberRepository memberRepository, HistoryRepository historyRepository, InquiryRepository inquiryRepository) {
        this.memberService = memberService;
        this.inquiryService = inquiryService;
        this.noticeService = noticeService;
        this.memberRepository = memberRepository;
        this.historyRepository = historyRepository;
        this.inquiryRepository = inquiryRepository;
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        Sort memberSort = Sort.by(Sort.Direction.ASC, "joinDate");
        List<Member> memberList = memberRepository.findAll(memberSort);
        Sort hisorySort = Sort.by(Sort.Direction.ASC, "date");
        List<History> historyList = historyRepository.findAll(hisorySort);

        int count = 0;
        LocalDate today = LocalDate.now();
        for (Member member : memberList) {
            if (member.getLastAccessTime().toLocalDate().isEqual(today)) {
                count++;
            }
        }
        History history = new History(today, count);
        historyList.add(history);

        List<HistoryDTO> historyDTOList = new ArrayList<>();

        for (History temp : historyList) {
            historyDTOList.add(new HistoryDTO(temp.getDate(), 0, temp.getVisitorNum()));
        }

        int i = 0;
        int j = 0;
        while (i < memberList.size()) {
            while (historyDTOList.get(j).getDate().isBefore(memberList.get(i).getJoinDate())) {
                j++;
            }
            historyDTOList.get(j).setMemberNum(historyDTOList.get(j).getMemberNum() + 1);
            i++;
        }
        model.addAttribute("historyList", historyDTOList);

        // 구성원 피라미드
        // 10대 이하, 20대, 30대, 40대, 50대, 60대 이상 남자
        // 10대 이하, 20대, 30대, 40대, 50대, 60대 이상 여자 순서
        int[] array = new int[12];
        for (Member member : memberList) {
            if (member.getSex() == null || member.getAge() == null) {
                continue;
            }
            int index = 0;
            if (Objects.equals(member.getSex(), "여")) {
                index += 6;
            }
            if (Objects.equals(member.getAge(), "20대")) {
                index += 1;
            } else if (Objects.equals(member.getAge(), "30대")) {
                index += 2;
            } else if (Objects.equals(member.getAge(), "40대")) {
                index += 3;
            } else if (Objects.equals(member.getAge(), "50대")) {
                index += 4;
            } else if (Objects.equals(member.getAge(), "60대 이상")) {
                index += 5;
            }
            array[index] += 1;
        }
        model.addAttribute("array", array);

        // 공지사항
        List<NoticeDTO> noticeDTOList = noticeService.getAllNotice();
        model.addAttribute("noticeList", noticeDTOList);

        // 개발자와의 대화
        Sort inquirySort = Sort.by(Sort.Direction.ASC, "member.indexNumber", "sendTime");
        List<Inquiry> inquiryList = inquiryRepository.findAllWithMemberFetch(inquirySort);
        List<InquiryObject> listOfLists = new ArrayList<>();

        List<InquiryDTO> inquiryDTOList = new ArrayList<>();
        LocalDate previousDate = null;
        if (!inquiryList.isEmpty()) {
            i = inquiryList.get(0).getMemberId();
            boolean isNewQuestion = inquiryList.get(0).getMember().isNewQuestion();
            boolean isToday = inquiryList.get(0).getSendTime().toLocalDate().isEqual(LocalDate.now());
            for (Inquiry inquiry : inquiryList) {
                // 새로운 상대와의 대화라면 (member_id가 다르다면) list에 저장 후 previousDate 초기화
                if (inquiry.getMemberId() != i) {
                    InquiryObject inquiryObject = new InquiryObject(i, isNewQuestion, isToday, inquiryDTOList);
                    listOfLists.add(inquiryObject);
                    inquiryDTOList = new ArrayList<>();
                    previousDate = null;
                }
                i = inquiry.getMemberId();
                isNewQuestion = inquiry.getMember().isNewQuestion();
                isToday = inquiry.getSendTime().toLocalDate().isEqual(LocalDate.now());

                LocalDate currentDate = inquiry.getSendTime().toLocalDate();
                InquiryDTO inquiryDTOTemp = new InquiryDTO();
                if (previousDate == null || !previousDate.equals(currentDate)) {
                    inquiryDTOTemp.setWrapper(true);
                    inquiryDTOTemp.setSendTime(inquiry.getSendTime());
                    inquiryDTOList.add(inquiryDTOTemp);
                    previousDate = currentDate;
                }
                inquiryDTOTemp = new InquiryDTO();
                inquiryDTOTemp.setWrapper(false);
                inquiryDTOTemp.setSendTime(inquiry.getSendTime());
                inquiryDTOTemp.setMessageType(inquiry.getMessageType());
                inquiryDTOTemp.setMessage(inquiry.getMessage().replace("\r\n", "<br>"));
                inquiryDTOList.add(inquiryDTOTemp);
            }
            // 마지막 상대와의 대화도 저장
            InquiryObject inquiryObject = new InquiryObject(i, isNewQuestion, isToday, inquiryDTOList);
            listOfLists.add(inquiryObject);
        }

        // 마지막 대화 시간을 기준으로 정렬
        listOfLists.sort((inquiryObject1, inquiryObject2) -> {
            InquiryDTO dto1 = inquiryObject1.getInquiryDTOList().get(inquiryObject1.getInquiryDTOList().size() - 1);
            InquiryDTO dto2 = inquiryObject2.getInquiryDTOList().get(inquiryObject2.getInquiryDTOList().size() - 1);
            return dto2.getSendTime().compareTo(dto1.getSendTime());
        });
        model.addAttribute("talkObjects", listOfLists);

        return "admin/main";
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class InquiryObject {
        private int memberId;
        private boolean newQuestion;
        private boolean today;
        private List<InquiryDTO> inquiryDTOList;
    }

    // notice
    @PostMapping("/admin/notice")
    public ResponseEntity<?> noticePost(@RequestParam("title") String title, @RequestParam("content") String content) {
        noticeService.createNotice(title, content);
        List<Member> memberList = memberRepository.findAll();
        for (Member member : memberList) {
            member.setNewNotice(true);
            memberRepository.save(member);
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/admin/notice")
    public ResponseEntity<?> noticePatch(@RequestParam("indexNumber") int indexNumber, @RequestParam("title") String title, @RequestParam("content") String content) {
        noticeService.updateNotice(indexNumber, title, content);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/notice")
    public ResponseEntity<?> noticeDelete(@RequestParam("indexNumber") int indexNumber) {
        noticeService.deleteNotice(indexNumber);
        return ResponseEntity.ok().build();
    }

    // inquiry
    @PostMapping("/admin/answer")
    public ResponseEntity<?> answer(@RequestParam("memberId") int memberId, @RequestParam("message") String message) {
        inquiryService.saveInquiry(memberId, message, "A");
        return ResponseEntity.ok().build();
    }

    // read question
    @GetMapping("/admin/read/question/{memberId}")
    public ResponseEntity<?> readQuestion(@PathVariable("memberId") int memberId) {
        memberService.readQuestion(memberId);
        return ResponseEntity.ok().build();
    }
}
