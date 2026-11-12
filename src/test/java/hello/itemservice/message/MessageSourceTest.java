package hello.itemservice.message;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// 메세지 기능은 스프링 부트가 MessageSource 빈을 자동 등록해줘야 사용할 수 있기에 @SpringBootTest를 붙여줌
@SpringBootTest
public class MessageSourceTest {

    @Autowired
    MessageSource messageSource;

    @Test
    void helloMessage() {
        // "hello" = code : 등록된 메시지
        // 1번째 null = args : {0}에 들어갈 매개 변수
        // 2번째 null = Locale : 영어로 설정하면 messages_en.properties의 "hello" 메시지를 들고옴
        // local을 설정하지 않았으므로 spring.messages.basename에 설정한 기본 파일을 읽어 들인다.
        String result = messageSource.getMessage("hello", null, null);
        assertThat(result).isEqualTo("안녕");
    }

    // 등록되지 않은 메세지를 찾았을 떄 예외 발생
    @Test
    void notFoundMessageCode() {
        assertThatThrownBy(() -> messageSource.getMessage("no_messages",null,null))
                .isInstanceOf(NoSuchMessageException.class);
    }

    // 등록되지 않은 메세지 취득 시, 디폴트 메시지 출력시키기
    @Test
    void notFoundMessageCodeDefaultMessage() {
        String defaultMessage = messageSource.getMessage("no_messages", null, "디폴트 메시지", null);
        assertThat(defaultMessage).isEqualTo("디폴트 메시지");
    }

    @Test
    void argumentMessage() {
        String message = messageSource.getMessage("hello.name", new Object[]{"김진영"}, null);
        assertThat(message).isEqualTo("안녕 김진영");
    }

    @Test
    void dafaultLocale() {
        String koreanMessage1 = messageSource.getMessage("hello", null, null);
        assertThat(koreanMessage1).isEqualTo("안녕");

        String koreanMessage2 = messageSource.getMessage("hello", null, Locale.KOREA);
        assertThat(koreanMessage2).isEqualTo("안녕");
    }

    // Locale.ENGLISH : message_en.properties를 읽어 들인다
    @Test
    void englishLocale() {
        String englishMessage = messageSource.getMessage("hello", null, Locale.ENGLISH);
        assertThat(englishMessage).isEqualTo("hello");
    }

}
