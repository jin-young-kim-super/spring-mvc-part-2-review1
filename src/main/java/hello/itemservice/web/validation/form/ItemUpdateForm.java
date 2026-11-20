package hello.itemservice.web.validation.form;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 상품 수정 DTO
 */
@Data
public class ItemUpdateForm {

    @NotNull
    private Long id;

    @NotBlank
    private String itemName;

    @NotNull
    @Range(min =1000 , max =100000)
    private Integer price;

    // 상품 수정에서는 수량에 제한 없음
    private Integer quantity;
}