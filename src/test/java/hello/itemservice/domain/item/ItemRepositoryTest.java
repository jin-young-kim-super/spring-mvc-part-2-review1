package hello.itemservice.domain.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.aggregator.AggregateWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ItemRepositoryTest {

    ItemRepository itemRepository = new ItemRepository();

    @AfterEach
    void afterEach() {
        itemRepository.clearStore();
    }

    @Test
    void save() {

        //given
        Item item = new Item("itemA",1000,10);

        //when
        Item savedItem = itemRepository.save(item);

        //then
        Item findItem = itemRepository.findById(savedItem.getId());
        assertThat(savedItem).isEqualTo(findItem);
    }


    @Test
    void findAll() {

        //given
        Item itemA = new Item("itemA",1000,10);
        Item itemB = new Item("itemB",2000,20);
        Item saveItemA = itemRepository.save(itemA);
        Item saveItemB = itemRepository.save(itemB);

        //when
        List<Item> findAll = itemRepository.findAll();


        //then
        assertThat(findAll.size()).isEqualTo(2);
        assertThat(findAll).contains(itemA,itemB);
    }

    @Test
    void updateItem() {

        //given
        Item item = new Item("itemA", 1000, 10);

        Item save = itemRepository.save(item);
        Long id = save.getId();

        //when
        Item updateItem = new Item("itemB", 2000, 20);
        itemRepository.update(id,updateItem);

        //then
        Item findItem = itemRepository.findById(id);

        assertThat(findItem.getItemName()).isEqualTo(updateItem.getItemName());
        assertThat(findItem.getPrice()).isEqualTo(updateItem.getPrice());
        assertThat(findItem.getQuantity()).isEqualTo(updateItem.getQuantity());
    }
}