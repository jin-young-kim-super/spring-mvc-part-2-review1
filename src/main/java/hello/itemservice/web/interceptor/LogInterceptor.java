package hello.itemservice.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    public static final String LOG_ID = "logId";

    // boolean : return false의 경우, 그 이후 컨트롤러 호출 자체를 안 한다.
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        // postHandle, afterCompletion에서도 일관성 있게 uuid를 출력하기 위해 HttpServletRequest 객체에 담는다
        // -> 스프링 인터셉터의 호출 순서가 1.preHandle -> 2.postHandle -> 3.afterHandle이기에 반드시 uuid는
        // preHandler에서 선언해줘야 한다.
        request.setAttribute(LOG_ID,uuid);

        log.info("REQUEST [{},{},{}]",uuid,requestURI,handler);


        // @RequestMapping 메서드가 호출될 경우: HandlerMethod
        // 정적 리소스가 호출될 경우 : ResourceHttpRequestHandler
        // -> preHandle은 HandlerMapping 완료 후 호출되기 떄문에 호출될 컨트롤러와 메서드의 모든 정보를 알 수가 있다.
        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            log.info("handlerMethod = {}", handlerMethod);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        // postHandler은 컨트롤러 실행 완료 후 ModelAndView가 반환된 후에 호출되므로,
        // ModelAndView 정보를 알 수가 있다.
        log.info("postHandle [{}]",modelAndView);
    }

    // Exception :
    // -> 예외 발생 x -> Exception == null
    // -> 예외 발생 o -> Exception != null
    // -> afterCompletion은 예외 발생 시에도 항상 호출되며, view의 render()까지 다 끝나고 호출되기에 예외 정보를 알 수가 있다.
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

        String requestURI = request.getRequestURI();
        Object logId = (String)request.getAttribute(LOG_ID);

        log.info("RESPONSE [{} {} {}]",logId,requestURI,handler);

        if(ex != null) {
            log.error("afterCompletion error!!",ex);
        }



    }
}
