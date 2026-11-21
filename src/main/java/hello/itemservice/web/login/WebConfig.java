package hello.itemservice.web.login;

import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 필터(Logfilter)를 @Component로 빈 등록 안(못) 하는 이유
 * -> 아래 코드를 보면 알겠듯이 필터 등록 시에는 필터 적용 순서, 허용 URL 패턴 등 여러 부가 정보를 등록해줘야 한다.
 * 그러한 우리가 LogFilter를 만들 떄 사용했던 서블릿 Filter에는 그러한 기능을 제공하지 않는다.
 * 그래서 @Configuration을 사용해서 수동 등록을 한다.
 */


@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LogFilter());
        filterFilterRegistrationBean.setOrder(1); // 필터 적용 순서
        filterFilterRegistrationBean.addUrlPatterns("/*"); // 필용 적용한 요청 URL
        return filterFilterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean loginCheckFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LoginCheckFilter());
        filterFilterRegistrationBean.setOrder(2); // 필터 적용 순서
        // whiteList에서 사실상 URL 필터링하고 있음
        filterFilterRegistrationBean.addUrlPatterns("/*"); // 필용 적용한 요청 URL
        return filterFilterRegistrationBean;
    }
}
