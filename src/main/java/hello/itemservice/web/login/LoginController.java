package hello.itemservice.web.login;

import hello.itemservice.domain.login.LoginService;
import hello.itemservice.domain.member.Member;
import hello.itemservice.web.SessionConst;
import hello.itemservice.web.login.session.SessionManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    /**
     * 여기에는 @Validated, @Valid을 붙이면 안된다.
     * -> @ModelAttribute LoginForm..을 사용하는 이유는 어디까지나 타임템플릿에
     * loginForm 객체를 집어 넣어서 @PostMapping(/login)(로그인 기능) 시에 검증 오류가 발생 했을 때
     * 그 오류 정보를 그대로 화면에 보여 주기 위한 것이다.
     * 만약 @Validated를 붙이게 되면 LoginForm의 @NotEmpty 등에 의해서 검증 오류가 발생하여 예외를 터트린다(물론, 매개변수에 BindingResult 객체가 선언되지 않았을 경우)
     */
    @GetMapping("/login")
    public String loginForm(@ModelAttribute LoginForm loginForm) {
        return "/login/loginForm";
    }

    //@PostMapping("/login")
    public String loginV1(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {

        if(bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        // 복합룰 검증
        if(loginMember == null) {
            bindingResult.reject("loginFail","아이디 또는 비밀번호가 잘못됐습니다.");
            return "login/loginForm";
        }

        // 세션 쿠키 : 쿠키 만료날짜를 입력하지 않으면 웹 브라우저 종료시까지 쿠키값 유효
        Cookie cookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(cookie);
        return "redirect:/";
    }

    //@PostMapping("/login")
    public String loginV2(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {

        if(bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        // 복합룰 검증
        if(loginMember == null) {
            bindingResult.reject("loginFail","아이디 또는 비밀번호가 잘못됐습니다.");
            return "login/loginForm";
        }

        sessionManager.createSession(loginMember,response);

        return "redirect:/";
    }

    //@PostMapping("/login")
    public String loginV3(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest request) {

        if(bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        // 복합룰 검증
        if(loginMember == null) {
            bindingResult.reject("loginFail","아이디 또는 비밀번호가 잘못됐습니다.");
            return "login/loginForm";
        }

        // 서블리이 제공하는 세션 기능 사용 : HttpSession 인터페이스를 Tomcat이 구현체로 만들어 세션 기능 제공
        // HttpSession.getSession() : 이전에 로그인 한 적이 있어서 메모리의 세션 테이블에 이미 해당 세션이 존재하면 그것을 반환하고,
        // 만약 이번이 처음 로그인이라서 세션 테이블에 세션이 없다면 세션을 세션 테이블에 생성해서 반환
        //※ Tomcat의 HttpServeltRequestfacade 객체에 있는 JESSIONID와 Member get해서 (JSESSIONID,Member)를 자동으로 세션 생성
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:/";
    }

    @PostMapping("/login")
    public String loginV4(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, @RequestParam(defaultValue = "/") String redirectURL, HttpServletRequest request) {

        if(bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        // 복합룰 검증
        if(loginMember == null) {
            bindingResult.reject("loginFail","아이디 또는 비밀번호가 잘못됐습니다.");
            return "login/loginForm";
        }

        // 서블리이 제공하는 세션 기능 사용 : HttpSession 인터페이스를 Tomcat이 구현체로 만들어 세션 기능 제공
        // HttpSession.getSession() : 이전에 로그인 한 적이 있어서 메모리의 세션 테이블에 이미 해당 세션이 존재하면 그것을 반환하고,
        // 만약 이번이 처음 로그인이라서 세션 테이블에 세션이 없다면 세션을 세션 테이블에 생성해서 반환
        //※ Tomcat의 HttpServeltRequestfacade 객체에 있는 JESSIONID와 Member get해서 (JSESSIONID,Member)를 자동으로 세션 생성
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:" + redirectURL;
    }

    //@PostMapping("/logout")
    public String logoutV1(HttpServletResponse response) {
        expireCookie(response);
        return "redirect:/";
    }

    //@PostMapping("/logout")
    public String logoutV2(HttpServletRequest request, HttpServletResponse response) {
        sessionManager.expire(request);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request, HttpServletResponse response) {
        // create = false
        // ->로그아웃 기능이니, 반드시 세션 테이블에 해당 세션이 저장돼 있어야 한다.
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    private static void expireCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("memberId", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
