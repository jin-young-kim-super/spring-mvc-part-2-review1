package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Item 도매인 객체 전용 검증 클래스
 * → DDD에서는 도메인 객체 별로 검증 클래스를 만드는 것이 좋다.
 */
@Component
public class ItemValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        // 검증 대상 도메인 객체가 Item 객체(Item 자식 객체 포함)가 맞냐?
        return Item.class.isAssignableFrom(clazz);
    }

    /**
     * Errors : BindingResult의 부모 클래스!!
     * -> 그래서 validate의 매개변수로 BindingResult가 넘어 올 수 있다.
     */
    @Override
    public void validate(Object target, Errors errors) {

        Item item = (Item) target;

        // 상품명 검증 로직(특정 필드 검증)
        if(!StringUtils.hasText(item.getItemName())) {
            errors.rejectValue("itemName","required");
        }

        // 상품 가격 검증 로직(특정 필드 검증)
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 100000) {
            errors.rejectValue("price","range",new Object[]{1000,100000},null);
        }

        // 상품 수량 검증 로직(특정 필드 검증)
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            errors.rejectValue("quantity","max",new Object[]{9999},null);
        }

        // 복합 룰 검증 : 너무 가격이 작은 주문은 안 받는다는 비지니스 요구사항에 대한 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                errors.reject("totalPriceMin",new Object[]{10000,resultPrice},null);
            }
        }

    }
}
