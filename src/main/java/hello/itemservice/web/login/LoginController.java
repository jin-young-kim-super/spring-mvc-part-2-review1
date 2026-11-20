package hello.itemservice.web.login;

import hello.itemservice.domain.login.LoginService;
import hello.itemservice.domain.member.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

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

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        // 복합룰 검증
        if(loginMember == null) {
            bindingResult.reject("loginFail","아이디 또는 비밀번호가 잘못됐습니다.");
            return "login/loginForm";
        }

        // TODO : 로그인 성공 처리
        return "redirect:/";
    }
}
