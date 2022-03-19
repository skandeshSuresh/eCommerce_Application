package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private final UserRepository userRepository = mock(UserRepository.class);

    private final OrderRepository orderRepository = mock(OrderRepository.class);

    private static final Logger log = LoggerFactory.getLogger(OrderControllerTest.class);

    @Before
    public void setUp(){

        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    @Order(1)
    public void submit_happy_path(){
        log.info("testing submitting order for a user");
        User user = createUser();

        Cart cart = createCart(user);
        user.setCart(cart);

        when(userRepository.findByUsername("testUsername")).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit("testUsername");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(BigDecimal.valueOf(200), Objects.requireNonNull(response.getBody()).getTotal());
        log.info("order has been submitted successfully");
    }

    @Test
    @Order(2)
    public void get_orders_for_user_happy_path(){
        log.info("testing getting all orders for a user");
        User user = createUser();
        List<UserOrder> orders = new ArrayList<>();

        when(orderRepository.findByUser(user)).thenReturn(orders);
        when(userRepository.findByUsername("testUsername")).thenReturn(user);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUsername");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(orders, response.getBody());
        log.info("all the orders are in order");
    }

    @Test
    @Order(3)
    public void submit_unhappy_path(){
        log.info("testing submitting order for a user (unhappy path, i.e., for a non-existent user");
//        submit orders for non-existent user
        ResponseEntity<UserOrder> response = orderController.submit("testUsername");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        log.warn("order could not be submitted");
    }

    @Test
    @Order(4)
    public void get_orders_for_user_unhappy_path(){
        log.info("testing getting all orders for a user (unhappy path, i.e., for a non-existent user");
//        get orders of a non-existent user
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUsername");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        log.warn("orders do not exist");
    }

//    helper methods
    public User createUser(){
        User user = new User();
        user.setId(0L);
        user.setUsername("testUsername");
        user.setPassword("password");
        return user;
    }

    public Cart createCart(User user){
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        cart.setTotal(BigDecimal.valueOf(200));

        return cart;
    }





}
