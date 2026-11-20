package hello.itemservice.domain.item;

import jakarta.validation.constraints.Max; // 자바 표준
import jakarta.validation.constraints.NotBlank; // 자바 표준
import jakarta.validation.constraints.NotNull; // 자바 표준
import lombok.Data;
import org.hibernate.validator.constraints.Range; // @Range는 하이버네이트서 제공
import org.hibernate.validator.constraints.ScriptAssert;

import java.util.List;

/**
 * Domain 모델 같은 경우 @Data를 사용하면 위험하다.
 * -> 그래서 DTO의 경우는 그냥 @Data를 사용해도 무방하다고 하지만, 도메인 객체 같은 경우 필요한 Lombok을 한정해서 사용!
 * (지금은 예제이니 @Data를 사용)
 */

@Data
// Hibernate에서 제공
//@ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >=10000", message = "총합이 10,000이 넘어야 합니다.")
public class Item {

    //@NotNull(groups = UpdateCehck.class) // 상품 수정 시에만 검증
    private Long id;

    /**
     * 상품 등록/상품 수정 시, 둘다 검증
     */
    //NotBlank(groups = {SaveCheck.class, UpdateCehck.class}) // 빈값 + 공백인지를 검증
    private String itemName;

    //@NotNull(groups = {SaveCheck.class, UpdateCehck.class})
    //@Range(min =1000 , max =100000)
    private Integer price;

    //@NotNull(groups = {SaveCheck.class, UpdateCehck.class})
    //@Max(value = 9999)
    private Integer quantity;

    /**
     * price,quantity에 int가 아닌 굳이 Integer를 사용하는 이유
     * -> price의 경우 아무 값이 안 들어 가는 경우, 만약 int를 사용하여 price=0이 들어 가는 경우를 방지하기 위해서이다.
     * 왜냐하면 가격이 0인 경우는 뭔가 실제 가격을 0원으로 책정된 것처럼 보일 수가 있다. 그래서 price가 세팅이 안 된 경우는 null이 입력되도록 Integer를 넣는다
     * ※quantity의 경우 0이 들어 가도 되서 그냥 사실 int를 사용해도 ㄱㅊ !
     */

    public Item() {}

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
