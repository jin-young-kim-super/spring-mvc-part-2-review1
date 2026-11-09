package hello.itemservice.domain.item;

import lombok.Data;

import java.util.List;

/**
 * Domain 모델 같은 경우 @Data를 사용하면 위험하다.
 * -> 그래서 DTO의 경우는 그냥 @Data를 사용해도 무방하다고 하지만, 도메인 객체 같은 경우 필요한 Lombok을 한정해서 사용!
 * (지금은 예제이니 @Data를 사용)
 */

@Data
public class Item {

    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;

    private Boolean open; // 판매 여부
    private List<String> regions; // 판매 지역
    private ItemType itemType; // 상품 종류
    private DeliveryCode deliveryCode; // 배송 방식

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
