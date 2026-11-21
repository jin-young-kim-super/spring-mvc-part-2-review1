package hello.itemservice.web;

import hello.itemservice.domain.login.LoginService;
import hello.itemservice.domain.member.Member;
import hello.itemservice.domain.member.MemberRepository;
import hello.itemservice.web.login.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

    //@GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model) {

        Member member = (Member)sessionManager.getSession(request);

        // 로그인 안 된 사용자
        if(member == null) {
            return "home";
        }

        model.addAttribute("member",member);
        return "loginHome";
    }

    @GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model) {

        // create : false
        // -> 로그인 화면에 처음 들어 왔는데, 자동으로 세션이 만들어 지면 안 된다.
        HttpSession session = request.getSession(false);
        if(session == null) {
            return "home";
        }

        Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        model.addAttribute("member",loginMember);
        return "loginHome";
    }
}