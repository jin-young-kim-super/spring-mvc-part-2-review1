package hello.itemservice.web;

import hello.itemservice.domain.login.LoginService;
import hello.itemservice.domain.member.Member;
import hello.itemservice.domain.member.MemberRepository;
import hello.itemservice.web.login.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final LoginService loginService;
    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

    //@GetMapping("/")
    public String home() {
        return "home";
    }

    // required = false
    // -> 로그인 안 된 사용자가 홈 화면에는 들어 올 수 있어야 한다.
    //@GetMapping("/")
    public String homeLoginV1(@CookieValue(name = "memberId",required = false) Long memberId, Model model) {

        // 로그인 안 된 사용자
        if(memberId == null) {
            return "home";
        }

        // 로그인 된 사용자
        Member loginMember = memberRepository.findById(memberId);
        if(loginMember == null) {
            return "home";
        }

        model.addAttribute("member",loginMember);
        return "loginHome";
    }

    @GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model) {

        Member member = (Member)sessionManager.getSession(request);

        // 로그인 안 된 사용자
        if(member == null) {
            return "home";
        }

        model.addAttribute("member",member);
        return "loginHome";
    }

}