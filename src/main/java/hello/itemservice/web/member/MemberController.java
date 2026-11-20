package hello.itemservice.web.member;

import hello.itemservice.domain.member.Member;
import hello.itemservice.domain.member.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberRepository memberRepository;

    /**
     * @ModelAttribute Member member == model.addAttribute("member",member)
     * -> 빈 Member 객체를 넣는 이유는 만약 @PostMapping("/add")(상품 등록)에서 validation 실패를 했을 때
     * 검증 실패 시 Member 객체에 어떤 필드에 어떤 오류가 나왔는지 타임리프를 이용해서 표시를 해야 하는데
     * 그떄 addMemberForm 안에 미리 Member 객체를 박아 놓음으로써 위 기능을 구현하기 위함
     */
    @GetMapping("/add")
    public String addForm(@ModelAttribute Member member) {
        return "/members/addMemberForm";
    }

    @PostMapping("/add")
    public String save(@Valid @ModelAttribute Member member, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            return "members/addMemberForm";
        }

        memberRepository.save(member);

        return "redirect:/";

    }
}
