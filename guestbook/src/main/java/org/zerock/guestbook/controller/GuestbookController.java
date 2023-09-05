package org.zerock.guestbook.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.guestbook.dto.GuestbookDTO;
import org.zerock.guestbook.dto.PageRequestDTO;
import org.zerock.guestbook.dto.PageResultDTO;
import org.zerock.guestbook.service.GuestbookService;

@Controller
@RequestMapping("/guestbook")
@Log4j2
@RequiredArgsConstructor // DI
public class GuestbookController {

    private final GuestbookService service;

    @GetMapping("/")
    public String index() {
        return "redirect:/guestbook/list";
    }

    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model) {

        // 스프링에서 자동으로 넘겨온 파라미터를 DTO에 맞게 자동으로 세팅 해주는듯?
        // next 값으로 size = 10 유지, page는 값이 11로 list를 호출

        log.info("list : " + pageRequestDTO);

        // GuestbookServiceImple로 흘러 들어감
        // 이후 리턴 해오는 값을 "result"에 담는 것
        model.addAttribute("result", service.getList(pageRequestDTO));
        // getList의 형태가 매우 까다롭다..
        // 결국 PageResultDTO 로 리턴이 되는 것
        // 정보는 총 페이지 번호
        // 현재 페이지 번호
        // 목록에 표시될 사이즈 크기
        // 시작 페이지 번호, 끝페이지 번호
        // 이전, 다음의 boolean
        // 페이지 번호 목록

        PageResultDTO dto = service.getList(pageRequestDTO);
        System.out.println("---------PageResultDTO Information-----------");
        System.out.println("total page : "+dto.getTotalPage());
        System.out.println("page : "+dto.getPage());
        System.out.println("pageList : "+dto.getPageList());
        System.out.println("start : "+dto.getStart());
        System.out.println("end : "+dto.getEnd());
        System.out.println("next : "+dto.isNext());
        System.out.println("prev : "+dto.isPrev());
        System.out.println("dtoList : "+dto.getDtoList());
        System.out.println("size : "+dto.getSize());


    }

    @GetMapping("/register")
    public void register() {
        log.info("register get");
    }

    @PostMapping("/register")
    public String registerPost(GuestbookDTO dto, RedirectAttributes redirectAttributes) {
        log.info("dto : " + dto);

        // 새로 추가된 엔티티의 번호
        Long gno = service.register(dto);

        // RedirectAttributes  -> 페이지가 넘어가면서 데이터나 메세지를 넘기고 싶은
        redirectAttributes.addFlashAttribute("msg", gno);

        return "redirect:/guestbook/list";
    }

    @GetMapping({"/read", "/modify"})
    public void read(Long gno, Model model, @ModelAttribute("requestDTO")PageRequestDTO requestDTO) {
        // PageRequestDTO 가 왜 필요 하지?

        log.info("gno : " + gno);

        System.out.println("test controller");
        GuestbookDTO dto = service.read(gno);


        model.addAttribute(
"dto", dto);
    }
    @PostMapping("/remove")
    public String remove(long gno, RedirectAttributes redirectAttributes) {

        log.info("remove : " + gno);

        service.remove(gno);

        redirectAttributes.addFlashAttribute("msg", gno);

        return "redirect:/guestbook/list";
    }

    @PostMapping("/modify")
    public String modify(GuestbookDTO dto,
                         @ModelAttribute("requestDTO") PageRequestDTO requestDTO,
                         RedirectAttributes redirectAttributes) {

        log.info("post modify dto : " + dto);

        service.modify(dto);

        redirectAttributes.addAttribute("page", requestDTO.getPage());
        redirectAttributes.addAttribute("type", requestDTO.getType());
        redirectAttributes.addAttribute("keyword", requestDTO.getKeyword());
        redirectAttributes.addAttribute("gno", dto.getGno());

        return "redirect:/guestbook/read";
    }

}
