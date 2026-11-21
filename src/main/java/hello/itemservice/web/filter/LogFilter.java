package hello.itemservice.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
    }

    /**
     * ServletReqyest : 이전 시간에도 언급했듯이 서블릿 필터와 스프링 인터셉터는 HttpServletRequest를 제공
     * -> 참고로 ServletRequest는 HttpServletRequest의 부모 인터페이스이다.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("log filter doFilter");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        String uuid = UUID.randomUUID().toString();

        try{
            log.info("REQUEST[{} , {}]",uuid,requestURI);
            chain.doFilter(request,response); // 이게 없으면 다음 필터(단계)로 넘어 가지를 못한다.
        }catch (Exception e) {
            throw e;
        } finally {
            log.info("RESPONSE[{} , {}]]",uuid,requestURI);
        }
    }

    @Override
    public void destroy() {
        log.info("log filter destroy");
    }
}
