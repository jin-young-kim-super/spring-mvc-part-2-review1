package hello.itemservice.web.argumentResolver;

import hello.itemservice.domain.member.Member;
import hello.itemservice.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


/**
 * 실행될 컨트롤러 메서드에 로그인된 Member 객체가 선언돼 있으면 그걸 넘겨 주는 ArgumentResolver
 */

@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    // HandlerMapping에 의해서 실행될 메서드가 결정되고, HandlerAdapter에 의해서 그걸 실행할 Adapter가 결정되면
    // 메서드의 의 모든 파라미터에 대해 supportsParater()를 적용해서 만약 아래 조건들을 만족하면 바이딩이 된다.
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("supportsParameter 실행");

        // 실행할 메서드의 파라미터에 @Login이 붙어 있냐 엾냐?
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);

        // 메서드의 해당 파라미터가 Member 타입이냐 아니냐?
        boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType());

        return hasLoginAnnotation && hasMemberType;
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {

        log.info("resolveArgument 실행");

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        HttpSession session = request.getSession(false);

        if(session == null) {
            // 세션이 없으면 Member 파라미터에 null이 들어감
            return null;
        }

        // 세션이 존재하면, 세션 테이블에 있는 Member 객체를 반환
        return (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

    }
}
