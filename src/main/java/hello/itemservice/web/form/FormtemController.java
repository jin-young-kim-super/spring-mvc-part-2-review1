package hello.itemservice.web.form;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/form/items")
@RequiredArgsConstructor
public class FormtemController {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items",items);
        return "form/items";
    }

    @GetMapping("{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item findItem = itemRepository.findById(itemId);
        model.addAttribute("item",findItem);

        Map<String,String> regions = new LinkedHashMap<>();
        regions.put("SEOUL","서울");
        regions.put("BUSAN","부산");
        regions.put("JEJU","제주");
        model.addAttribute("regions",regions);

        return "form/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item",new Item());

        Map<String,String> regions = new LinkedHashMap<>();
        regions.put("SEOUL","서울");
        regions.put("BUSAN","부산");
        regions.put("JEJU","제주");
        model.addAttribute("regions",regions);

        return "form/addForm";
    }

    //@PostMapping("/add")
    public String addItemV1(
            @RequestParam String itemName,
            @RequestParam Integer price,
            @RequestParam int quantity,
            Model model
    ) {
        Item item = new Item(itemName, price, quantity);
        Item savedItem = itemRepository.save(item);
        model.addAttribute("item",savedItem);
        return "form/item";
    }

    //@PostMapping("/add")
    public String addItemV2(
            @ModelAttribute Item item,
            Model model
    ) {
        Item savedItem = itemRepository.save(item);
        model.addAttribute("item",savedItem);
        return "form/item";
    }

    /**
     * @ModelAttribute("item") : ("item")을 설정하면, 자동으로 Model에 item이라는 이름으로 뷰 템플릿으로
     * 넘어 간다.
     */
    //@PostMapping("/add")
    public String addItemV3(@ModelAttribute("item") Item item) {
        itemRepository.save(item);
        return "form/item";
    }

    /**
     * @ModelAttribute("item") 생략: 객체명(Item)의 앞글자를 소문자로 바꿔서 Model에 "item"이라는 이름으로 자동 추가
     * -> 이런 생략은 너무 과하다.
     */
    //@PostMapping("/add")
    public String addItemV4(Item item) {
        itemRepository.save(item);
        return "form/item";
    }

    /**
     * "redirect:/basic/items/" + item.getId()의 문제점 : 만약에 item.getItemName()를 라다이렉트 URL로 설정을 한다면?
     * -> 인코딩 문제가 생긴다. 오늘은 redirect 시 인코딩 문제를 없애는 방법을 학습한다.
     */
    //@PostMapping("/add")
    public String addItemV5(Item item) {
        itemRepository.save(item);
        return "redirect:/form/items/" + item.getId();
    }

    // ..../baisc/items?status=true로 리다이렉트됨(물론, 인코딩 문제도 해결됨)
    @PostMapping("/add")
    public String addItemV6(Item item, RedirectAttributes redirectAttributes) {

        log.info("item.open={}",item.getOpen());

        Item savedItem = itemRepository.save(item);
        // {itemId}에 자동으로 saveItem.getId()가 들어감
        redirectAttributes.addAttribute("itemId",savedItem.getId());
        // ?staus=true의 형태로 쿼리 파라미터 형태로 들어감
        redirectAttributes.addAttribute("staus",true);
        return "redirect:/form/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item findItem = itemRepository.findById(itemId);
        model.addAttribute("item",findItem);

        Map<String,String> regions = new LinkedHashMap<>();
        regions.put("SEOUL","서울");
        regions.put("BUSAN","부산");
        regions.put("JEJU","제주");
        model.addAttribute("regions",regions);

        return "/form/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        Item updateParam = itemRepository.findById(itemId);
        updateParam.setItemName(updateParam.getItemName());
        updateParam.setPrice(updateParam.getPrice());
        updateParam.setQuantity(updateParam.getQuantity());
        updateParam.setOpen(updateParam.getOpen());
        updateParam.setRegions(updateParam.getRegions());
        updateParam.setItemType(updateParam.getItemType());
        updateParam.setDeliveryCode(updateParam.getDeliveryCode());
        return "redirect:/form/items/{itemId}";
    }
}