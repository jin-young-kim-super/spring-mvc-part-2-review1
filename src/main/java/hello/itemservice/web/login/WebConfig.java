package hello.itemservice.web.login;

import hello.itemservice.web.filter.LogFilter;
import hello.itemservice.web.filter.LoginCheckFilter;
import hello.itemservice.web.interceptor.LogInterceptor;
import hello.itemservice.web.interceptor.LoginCheckInterceptor;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 필터(Logfilter)를 @Component로 빈 등록 안(못) 하는 이유
 * -> 아래 코드를 보면 알겠듯이 필터 등록 시에는 필터 적용 순서, 허용 URL 패턴 등 여러 부가 정보를 등록해줘야 한다.
 * 그러한 우리가 LogFilter를 만들 떄 사용했던 서블릿 Filter에는 그러한 기능을 제공하지 않는다.
 * 그래서 @Configuration을 사용해서 수동 등록을 한다.
 */


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**","/*.ico","/error");

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                // 이게 서블릿 필터를 사용하지 않고 스프링 인터셉터를 사용하는 이유이다.
                // -> 서블릿 필터는 배제할 URL을 지정하는 기능이 없어서 whitelist를 별도로 만들어야 했는데
                // 스프링 인터셉터는 등록할 떄 그냥 바로 설정이 가능하다.
                .excludePathPatterns(
                        "/", "/members/add", "/login", "/logout",
                        "/css/**", "/*.ico", "/error"
                );
    }

    //@Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LogFilter());
        filterFilterRegistrationBean.setOrder(1); // 필터 적용 순서
        filterFilterRegistrationBean.addUrlPatterns("/*"); // 필용 적용한 요청 URL
        return filterFilterRegistrationBean;
    }

    //@Bean
    public FilterRegistrationBean loginCheckFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LoginCheckFilter());
        filterFilterRegistrationBean.setOrder(2); // 필터 적용 순서
        // whiteList에서 사실상 URL 필터링하고 있음
        filterFilterRegistrationBean.addUrlPatterns("/*"); // 필용 적용한 요청 URL
        return filterFilterRegistrationBean;
    }
}
