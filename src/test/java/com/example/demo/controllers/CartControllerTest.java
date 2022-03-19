package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private final UserRepository userRepository = mock(UserRepository.class);

    private final CartRepository cartRepository = mock(CartRepository.class);

    private final ItemRepository itemRepository = mock(ItemRepository.class);

    private static final Logger log = LoggerFactory.getLogger(CartControllerTest.class);

    @Before
    public void setUp(){

        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    @Order(1)
    public void add_to_cart_happy_path(){
        log.info("testing adding an item to the cart of a user");
        Cart cart = new Cart();
        User user = createUser(cart);
        cart.setUser(user);

        log.info("creating new item");
        Item item = createItem();

        log.info("creating new object of class ModifyCartRequest");
        ModifyCartRequest modifyCartRequest = createCartRequest();

        when(userRepository.findByUsername("testUsername")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        assertNotNull(response.getBody());
        assertTrue(cart.getItems().contains(item));
        log.info("item has been added to the cart successfully");
    }

    @Test
    @Order(2)
    public void remove_from_cart_happy_path(){
        log.info("testing removing an item from the cart of a user");
        Cart cart = new Cart();
        User user = createUser(cart);
        cart.setUser(user);

        log.info("creating new item");
        Item item = createItem();

        log.info("creating new object of class ModifyCartRequest");
        ModifyCartRequest modifyCartRequest = createCartRequest();

        when(userRepository.findByUsername("testUsername")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        assertNotNull(response.getBody());
        assertFalse(response.getBody().getItems().contains(item));
        log.info("item has been removed from the cart successfully");
    }

    @Test
    @Order(3)
    public void add_to_cart_unhappy_path(){
        log.info("testing adding an item to the cart of a user (unhappy path, i.e. for non-existent user)");
//        add to cart for a non-existent user

        log.info("creating new item");
        Item item = createItem();

        log.info("creating new object of class ModifyCartRequest");
        ModifyCartRequest modifyCartRequest = createCartRequest();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        log.warn("could not add to cart of non-existent user");
    }

    @Test
    @Order(4)
    public void remove_from_cart_unhappy_path(){
        log.info("testing removing an item from the cart of a user (unhappy path, i.e. for non-existent user)");
//        remove from cart for a non-existent user
        log.info("creating new item");
        Item item = createItem();

        log.info("creating new object of class ModifyCartRequest");
        ModifyCartRequest modifyCartRequest = createCartRequest();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        log.warn("could not remove from cart of non-existent user");

    }

//    helper methods
    public User createUser(Cart cart){
        User user = new User();
        user.setId(0L);
        user.setUsername("testUsername");
        user.setPassword("password");
        user.setCart(cart);

        return user;
    }

    public Item createItem(){
        Item item = new Item();
        item.setId(1L);
        item.setName("testItem");
        item.setPrice(new BigDecimal(100));
        item.setDescription("This is a test item");

        return item;
    }

    public ModifyCartRequest createCartRequest(){
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("testUsername");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(2);

        return modifyCartRequest;
    }
}
