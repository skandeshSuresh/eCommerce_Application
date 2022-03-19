package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private final ItemRepository itemRepository = mock(ItemRepository.class);

    private static final Logger log = LoggerFactory.getLogger(ItemControllerTest.class);

    @Before
    public void setUp(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    @Order(1)
    public void get_items_happy_path(){
        log.info("testing getting all the items in the repository");
        when(itemRepository.findAll()).thenReturn(new ArrayList<>());

        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(new ArrayList<>(), response.getBody());
        log.info("all the items are in order");
    }

    @Test
    @Order(2)
    public void get_item_by_id_happy_path(){
        log.info("testing getting items by id in the repository");
        Item item = createItem();

        when(itemRepository.findById(0L)).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(0L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(item, response.getBody());
        log.info("item with id 1 has been found");
    }

    @Test
    @Order(3)
    public void get_items_by_name(){
        log.info("testing getting items by item name");
        Item item = createItem();
        List<Item> items = new ArrayList<>();
        items.add(item);

        when(itemRepository.findByName("testItem")).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("testItem");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(items, response.getBody());
        log.info("item list is as expected");
    }

//    helper methods
    public Item createItem(){
        Item item = new Item();
        item.setId(1L);
        item.setName("testItem");
        item.setPrice(new BigDecimal(100));
        item.setDescription("This is a test item");

        return item;
}
}
