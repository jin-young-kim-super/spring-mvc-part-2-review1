package hello.itemservice.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageCodesResolverTest {


    MessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();

    // new FieldError
    @Test
    void setMessageCodesResolver_Field() {
        String[] messageCodes = messageCodesResolver.resolveMessageCodes("required", "item", "itemName", String.class);
        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
        }
        /**
         * messageCodesResolver.resolveMessageCodes("required", "item", "itemName", String.class) 내부 동작
         * -> "itemName"이라는 필드명을 넣어 줬기 때문에 BindinResult.addErros(new FieldError(...new String[]{"required.item.itemName","required.itemName",.....}))이 자동 생성
         * 출력 결과
         * messageCode = required.item.itemName(우선 순위1)
         * messageCode = required.itemName(우선 순위2)
         * messageCode = required.java.lang.String(우선 순위3) -
         * messageCode = required(우선 순위4)
         */
        assertThat(messageCodes).contains("required.item.itemName","required.itemName","required.java.lang.String","required");
    }

    // new ObjectError
    @Test
    void setMessageCodesResolver_Object() {
        String[] messageCodes = messageCodesResolver.resolveMessageCodes("required", "item");
        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
        }
        /**
         * messageCodesResolver.resolveMessageCodes("required", "item")의 내부 동작
         * -> BindingResult.addErrors(new ObjectError("item",new String[]{"required","required.item"}))을 자동 생성!
         * 출력 결과
         * messageCode = required.item(우선 순위1)
         * messageCode = required(우선 순위2)
         */
        assertThat(messageCodes).contains("required.item","required");
    }
}
