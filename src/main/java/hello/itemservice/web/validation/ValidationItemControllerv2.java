package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerv2 {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;

    /**
     * 이 컨트롤러가 호출될 때마다 init()이 호출됨
     * -> 고로, WebDataBinder 객체와 그 안에 들어 가는 ItemValidator 객체가 항상 새로 생성된다.
     * (물론, 모든 컨트롤러에 대해 클로벌하게 적용하는 방법도 있다. 그건 나중에!)
     */
    @InitBinder
    public void init(WebDataBinder dataBinder) {
        dataBinder.addValidators(itemValidator);
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items",items);
        return "validation/v2/items";
    }

    @GetMapping("{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item findItem = itemRepository.findById(itemId);
        model.addAttribute("item",findItem);

        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item",new Item());

        return "validation/v2/addForm";
    }

    //@PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 검증 오류 정보 보관
        Map<String,String> errors = new HashMap<>();

        /**
         * 검증 로직 시작(특정 필드, 복합 룰 검증)
         */
        // 상품명 검증 로직(특정 필드 검증)
        if(!StringUtils.hasText(item.getItemName())) {
            //errors.put("itemName","상품 이름은 필수입니다.");
            bindingResult.addError(new FieldError("item","itemName","상품 이름은 필수입니다."));
        }

        // 상품 가격 검증 로직(특정 필드 검증)
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 100000) {
            //errors.put("price","가격은 1,000 ~ 1,00000까지 허용합니다.");
            bindingResult.addError(new FieldError("item","price","가격은 1,000 ~ 1,00000까지 허용합니다."));
        }

        // 상품 수량 검증 로직(특정 필드 검증)
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            //errors.put("quantity","수량은 최대 9,999까지 허용합니다.");
            bindingResult.addError(new FieldError("item","quantity","수량은 최대 9,999까지 허용합니다."));
        }

        // 복합 룰 검증 : 너무 가격이 작은 주문은 안 받는다는 비지니스 요구사항에 대한 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                //errors.put("globalError","가격 * 수량의 합은 10,000원 이상이여야 합니다. 현재 값 : " + resultPrice);
                bindingResult.addError(new ObjectError("item","가격 * 수량의 합은 10,000원 이상이여야 합니다. 현재 값 : " + resultPrice));
            }
        }

        // 검증 시 오류가 있는 경우 사용자에게 입력값을 그대로 보내고 다시 등록 폼으로 돌아가게 해야 한다.
        if(bindingResult.hasErrors()) {
            log.info("errors={}",bindingResult);
            //model.addAttribute("errors",errors);
            // -> BindingResult는 자동으로 Model에 담긴다
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        // {itemId}에 자동으로 saveItem.getId()가 들어감
        redirectAttributes.addAttribute("itemId",savedItem.getId());
        // ?staus=true의 형태로 쿼리 파라미터 형태로 들어감
        redirectAttributes.addAttribute("staus",true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    //@PostMapping("/add")
    public String addItemv2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 검증 오류 정보 보관
        //Map<String,String> errors = new HashMap<>();

        /**
         * 검증 로직 시작(특정 필드, 복합 룰 검증)
         */
        // 상품명 검증 로직(특정 필드 검증)
        if(!StringUtils.hasText(item.getItemName())) {
            //bindingResult.addError(new FieldError("item","itemName","상품 이름은 필수입니다."));
            bindingResult.addError(new FieldError("item","itemName",item.getItemName(),false,null,null,"상품 이름은 필수입니다."));
        }

        // 상품 가격 검증 로직(특정 필드 검증)
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 100000) {
            //bindingResult.addError(new FieldError("item","price","가격은 1,000 ~ 1,00000까지 허용합니다."));
            bindingResult.addError(new FieldError("item","price",item.getPrice(),false,null,null,"가격은 1,000 ~ 1,00000까지 허용합니다."));
        }

        // 상품 수량 검증 로직(특정 필드 검증)
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            //bindingResult.addError(new FieldError("item","quantity","수량은 최대 9,999까지 허용합니다."));
            bindingResult.addError(new FieldError("item","quantity",item.getQuantity(),false,null,null,"수량은 최대 9,999까지 허용합니다."));
        }

        // 복합 룰 검증 : 너무 가격이 작은 주문은 안 받는다는 비지니스 요구사항에 대한 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                //bindingResult.addError(new ObjectError("item","가격 * 수량의 합은 10,000원 이상이여야 합니다. 현재 값 : " + resultPrice));
                bindingResult.addError(new ObjectError("item",null,null,"가격 * 수량의 합은 10,000원 이상이여야 합니다. 현재 값 : " + resultPrice));
            }
        }

        // 검증 시 오류가 있는 경우 사용자에게 입력값을 그대로 보내고 다시 등록 폼으로 돌아가게 해야 한다.
        if(bindingResult.hasErrors()) {
            log.info("errors={}",bindingResult);
            //model.addAttribute("errors",errors);
            // -> BindingResult는 자동으로 Model에 담긴다
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        // {itemId}에 자동으로 saveItem.getId()가 들어감
        redirectAttributes.addAttribute("itemId",savedItem.getId());
        // ?staus=true의 형태로 쿼리 파라미터 형태로 들어감
        redirectAttributes.addAttribute("staus",true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    //@PostMapping("/add")
    public String addItemv3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        /**
         * 검증 로직 시작(특정 필드, 복합 룰 검증)
         */
        // 상품명 검증 로직(특정 필드 검증)
        if(!StringUtils.hasText(item.getItemName())) {
            //bindingResult.addError(new FieldError("item","itemName",item.getItemName(),false,null,null,"상품 이름은 필수입니다."));
            bindingResult.addError(new FieldError("item","itemName",item.getItemName(),false,new String[]{"required.item.itemName"},null,null));
        }

        // 상품 가격 검증 로직(특정 필드 검증)
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 100000) {
            //bindingResult.addError(new FieldError("item","price",item.getPrice(),false,null,null,"가격은 1,000 ~ 1,00000까지 허용합니다."));
            bindingResult.addError(new FieldError("item","price",item.getPrice(),false,new String[]{"range.item.price"},new Object[]{1000,100000},null));
        }

        // 상품 수량 검증 로직(특정 필드 검증)
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            //bindingResult.addError(new FieldError("item","quantity",item.getQuantity(),false,null,null,"수량은 최대 9,999까지 허용합니다."));
            bindingResult.addError(new FieldError("item","quantity",item.getQuantity(),false,new String[]{"max.item.quantity"},new Object[]{9999},null));
        }

        // 복합 룰 검증 : 너무 가격이 작은 주문은 안 받는다는 비지니스 요구사항에 대한 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                //bindingResult.addError(new ObjectError("item",null,null,"가격 * 수량의 합은 10,000원 이상이여야 합니다. 현재 값 : " + resultPrice));
                bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"},new Object[]{10000,resultPrice},null));
            }
        }

        // 검증 시 오류가 있는 경우 사용자에게 입력값을 그대로 보내고 다시 등록 폼으로 돌아가게 해야 한다.
        if(bindingResult.hasErrors()) {
            log.info("errors={}",bindingResult);
            //model.addAttribute("errors",errors);
            // -> BindingResult는 자동으로 Model에 담긴다
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        // {itemId}에 자동으로 saveItem.getId()가 들어감
        redirectAttributes.addAttribute("itemId",savedItem.getId());
        // ?staus=true의 형태로 쿼리 파라미터 형태로 들어감
        redirectAttributes.addAttribute("staus",true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    //@PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 출력 결과 : Item
        log.info("BindingResult의 검증 객체 = {}",bindingResult.getObjectName());

        /**
         * 검증 로직 시작(특정 필드, 복합 룰 검증)
         */
        // 상품명 검증 로직(특정 필드 검증)
        if(!StringUtils.hasText(item.getItemName())) {
            //bindingResult.addError(new FieldError("item","itemName",item.getItemName(),false,new String[]{"required.item.itemName"},null,null));
            bindingResult.rejectValue("itemName","required");
        }

        // 상품 가격 검증 로직(특정 필드 검증)
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 100000) {
            //bindingResult.addError(new FieldError("item","price",item.getPrice(),false,new String[]{"range.item.price"},new Object[]{1000,100000},null));
            bindingResult.rejectValue("price","range",new Object[]{1000,100000},null);
        }

        // 상품 수량 검증 로직(특정 필드 검증)
        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            //bindingResult.addError(new FieldError("item","quantity",item.getQuantity(),false,new String[]{"max.item.quantity"},new Object[]{9999},null));
            bindingResult.rejectValue("quantity","max",new Object[]{9999},null);
        }

        // 복합 룰 검증 : 너무 가격이 작은 주문은 안 받는다는 비지니스 요구사항에 대한 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
               //bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"},new Object[]{10000,resultPrice},null));
                bindingResult.reject("totalPriceMin",new Object[]{10000,resultPrice},null);
            }
        }

        // 검증 시 오류가 있는 경우 사용자에게 입력값을 그대로 보내고 다시 등록 폼으로 돌아가게 해야 한다.
        if(bindingResult.hasErrors()) {
            log.info("errors={}",bindingResult);
            //model.addAttribute("errors",errors);
            // -> BindingResult는 자동으로 Model에 담긴다
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        // {itemId}에 자동으로 saveItem.getId()가 들어감
        redirectAttributes.addAttribute("itemId",savedItem.getId());
        // ?staus=true의 형태로 쿼리 파라미터 형태로 들어감
        redirectAttributes.addAttribute("staus",true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    //@PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        itemValidator.validate(item,bindingResult);

        if(bindingResult.hasErrors()) {
            log.info("errors={}",bindingResult);
            //model.addAttribute("errors",errors);
            // -> BindingResult는 자동으로 Model에 담긴다
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        // {itemId}에 자동으로 saveItem.getId()가 들어감
        redirectAttributes.addAttribute("itemId",savedItem.getId());
        // ?staus=true의 형태로 쿼리 파라미터 형태로 들어감
        redirectAttributes.addAttribute("staus",true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     * @Validated : 반드시 붙여 줘야 validator가 적용된다.
     * -> @Validated가 없다고 가정해보자. 그럼 @InitBinder에 의해서 해당 컨트롤러 내의 모든 객체에 대해서
     * validation이 일어난다. 그러나 개발을 하다 보면 validation을 적용하고 싶지 않는 경우가 있다. 아마 그런 상황을 위해서 스프링이
     * @Validated를 붙여야만 검증이 일어나게 만든 것이 아닐까?
     * ※@Validated대신 @Valid로 해도 ㄱㅊ!
     * -> @Validated는 스프링이 제공하는 것이고, @Valid는 자바 표준 에노테이션이다.
     * 다만 @Valid를 사용하려면, build.gradle에 의존관계를 추가해야 한다.
     *
     * Q. @Validated가 붙은 객체에 대해서 과연 Webdatabinder는 대체 어떻게 적정한 validotor를 고를까?
     * -> 그게 바로 Validator 인터페이스가 제공하는 support()메서드이다.
     */
    @PostMapping("/add")
    public String addItemV6(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        if(bindingResult.hasErrors()) {
            log.info("errors={}",bindingResult);
            //model.addAttribute("errors",errors);
            // -> BindingResult는 자동으로 Model에 담긴다
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        // {itemId}에 자동으로 saveItem.getId()가 들어감
        redirectAttributes.addAttribute("itemId",savedItem.getId());
        // ?staus=true의 형태로 쿼리 파라미터 형태로 들어감
        redirectAttributes.addAttribute("staus",true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item findItem = itemRepository.findById(itemId);
        model.addAttribute("item",findItem);
        return "/validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.updateItem(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }
}