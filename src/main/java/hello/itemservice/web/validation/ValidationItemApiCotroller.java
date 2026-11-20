package hello.itemservice.web.validation;

import hello.itemservice.web.validation.form.ItemSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @RequestBody에 대해서도 Bean Validation 사용 가능
 * -> 그러나 @ModelAttribute, @RequestParam 사용시와 다르게 매우 큰 차이점이 있다(메모장 참고)
 */

@Slf4j
@RestController // @RestController = @Controller + @ResponseBody
@RequestMapping("/validation/api/items")
public class ValidationItemApiCotroller {

    //@ResponseBody
    @PostMapping("/add")
    public Object addItem(@RequestBody @Validated ItemSaveForm form, BindingResult bindingResult) {

        log.info("API 컨트롤러 호출");

        if(bindingResult.hasErrors()) {
            log.info("검증 오류 발생 errors={}",bindingResult);
            return bindingResult.getAllErrors();
        }

        log.info("성공 로직 실행");

        return form;

    }
}
