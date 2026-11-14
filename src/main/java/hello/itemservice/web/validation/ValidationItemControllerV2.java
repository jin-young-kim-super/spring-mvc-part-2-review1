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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;

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

    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 검증 오류 정보 보관
        Map<String,String> errors = new HashMap<>();

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