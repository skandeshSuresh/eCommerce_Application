package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp(){

        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submit_happy_path(){
        User user = new User();
        user.setUsername("testUserName");

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        cart.setTotal(BigDecimal.valueOf(200));

        user.setCart(cart);

        when(userRepository.findByUsername("testUsername")).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit("testUsername");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(BigDecimal.valueOf(200), response.getBody().getTotal());
    }

    @Test
    public void get_orders_for_user_happy_path(){
        User user = new User();
        user.setUsername("testUserName");
        List<UserOrder> orders = new ArrayList<>();

        when(orderRepository.findByUser(user)).thenReturn(orders);
        when(userRepository.findByUsername("testUsername")).thenReturn(user);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUsername");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(orders, response.getBody());
    }

    @Test
    public void submit_unhappy_path(){
//        submit orders for non-existent user

        ResponseEntity<UserOrder> response = orderController.submit("testUsername");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void get_orders_for_user_unhappy_path(){
//        get orders of a non-existent user

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUsername");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

}
