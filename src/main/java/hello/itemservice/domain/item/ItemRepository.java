package hello.itemservice.domain.item;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class ItemRepository {

    private static final Map<Long,Item> store = new HashMap<>();
    private static long sequence = 0L;

    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        return store.get(id);
    }

    public List<Item> findAll() {
        return new ArrayList<>(store.values());
    }

    public void updateItem(Long itemId, Item updateItem) {
        Item findItem = findById(itemId);
        findItem.setItemName(updateItem.getItemName());
        findItem.setPrice(updateItem.getPrice());
        findItem.setQuantity(updateItem.getQuantity());
    }

    /**
     *  업데이트시 제일 정석을 Update용 Item DTO을 따로 만드는 것이다.
     *  -> 예를 들어 Item의 id값은 Update 대상값이 아니다. id 뿐만 아니라 Item 도매인 객체에는 update 대상이 아닌 값이 많다.
     *  만약 DTO를 만들지 않을 경우 개발자는 "? id 값은 setter호출해서 변경 안 해도 되나?"하며 실제로 해버리는 개발자가 있다.
     *  그래서 DTO를 만들어서 실제 update 대상 값만 정의해 놓고 하는 것이 정석이다.
     */
//    public void updateItemv2(Long itemId, UpdateItem updateItem) {
//        Item findItem = findById(itemId);
//        findItem.setItemName(updateItem.getItemName());
//        findItem.setPrice(updateItem.getPrice());
//        findItem.setQuantity(updateItem.getQuantity());
//    }


    public void clearStore() {
        store.clear();
    }
}
