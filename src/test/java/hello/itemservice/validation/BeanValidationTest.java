package hello.itemservice.validation;

import hello.itemservice.domain.item.Item;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation; // 자바 표준
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class BeanValidationTest {


    @Test
    void beanValidation() {
        /**
         * Bean Validation을 직접 사용!!!!
         * -> 테스트여서 직접 사용하고 있으나, 스프링은 이런 코드들을 이미 프레임워크내에서 알아서 자동 생성!!
         * Bean Validation을 직접 사용하는 예시)
         * ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
         * Validator validator = factory.getValidator();
         * // 생략
         * Set<ConstraintViolation<Item>> violations = validator.validate(item) // 이 부분이 Bean Validation을 직접 사용하는 부분
         */
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Item item = new Item();
        item.setItemName("    ");
        item.setPrice(0);
        item.setQuantity(10000);

        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        for (ConstraintViolation<Item> violation : violations) {
            System.out.println("violation = " + violation);
            System.out.println("violation = " + violation.getMessage());
        }
    }

    /**
     * 출력 결과(오류 메시지는 jakarta.validation 패키지에서 기본 제공)
     * violation = ConstraintViolationImpl{interpolatedMessage='공백일 수 없습니다', propertyPath=itemName, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{jakarta.validation.constraints.NotBlank.message}'}
     * violation = 공백일 수 없습니다
     *
     * violation = ConstraintViolationImpl{interpolatedMessage='1000에서 100000 사이여야 합니다', propertyPath=price, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{org.hibernate.validator.constraints.Range.message}'}
     * violation = 1000에서 100000 사이여야 합니다
     *
     * violation = ConstraintViolationImpl{interpolatedMessage='9999 이하여야 합니다', propertyPath=quantity, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{jakarta.validation.constraints.Max.message}'}
     * violation = 9999 이하여야 합니다
     */

}
