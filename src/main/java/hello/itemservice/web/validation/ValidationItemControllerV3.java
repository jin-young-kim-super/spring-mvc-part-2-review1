package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v3/items")
@RequiredArgsConstructor
public class ValidationItemControllerV3 {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items",items);
        return "validation/v3/items";
    }

    @GetMapping("{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item findItem = itemRepository.findById(itemId);
        model.addAttribute("item",findItem);

        return "validation/v3/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item",new Item());

        return "validation/v3/addForm";
    }

    /**
     * 자바 표준의 Bean Validation도 검증 객체 앞에 @Validated 혹은 @Valid를 붙여 줘야 한다.
     * ->
     * Q. 어?! 스프링의 Validator 사용시에는 @InitBinder로 validator를 등록해줘야 했는데, 자바 표준 Bean Validation은 아무 등록도 안 해 줬는데 왜 동작하지?
     * A. 스프링 부트는 자바 표준의 Bean Validation을 글로벌 적용한다. 그래서 @InitBinder를 통해서 validator 등록을 안 해도 모든 컨트롤러에서 사용이 가능.
     * 그 다음 흐름은 rejectValue(), FieldError, ObjectError를 똑같이 이용하여 최종 내용을 BindingResulg 객체에 담아 준다.
     * ※아마 @NotBlank, @Max 등의 검증은 어느 컨트롤러에서도 일반적으로 공통적으로 사용하다보니, 스프링 부트가 글로벌 설정을 채택한 듯?
     * ※@Validated : 스프링 제공
     * ※@Valide : 자바 표준 제공 -> 스프링 프레임워크가 아니여도 동작 가능을 시사.
     *
     * Bean Validation의 validator는 객체(ex.Item)에 입력값이 바인딩된 다음에야 적용이 된다.
     * -> 당연한 이야기이다. 예를 들어 가격에 "ㅂㅂㅂㅂ"를 입력되어서 바인딩이 되지도 않았는데, 검증을 해도 의미가 없다.
     */
    @PostMapping("/add")
    public String addItemV6(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        if(bindingResult.hasErrors()) {
            log.info("errors={}",bindingResult);
            //model.addAttribute("errors",errors);
            // -> BindingResult는 자동으로 Model에 담긴다
            return "validation/v3/addForm";
        }

        Item savedItem = itemRepository.save(item);
        // {itemId}에 자동으로 saveItem.getId()가 들어감
        redirectAttributes.addAttribute("itemId",savedItem.getId());
        // ?staus=true의 형태로 쿼리 파라미터 형태로 들어감
        redirectAttributes.addAttribute("staus",true);
        return "redirect:/validation/v3/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item findItem = itemRepository.findById(itemId);
        model.addAttribute("item",findItem);
        return "/validation/v3/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.updateItem(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }
}