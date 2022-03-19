package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){

        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void create_user_happy_path() throws Exception{
        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testUsername");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("testUsername", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    public void find_by_user_name_happy_path(){
        User user = new User();
        user.setUsername("testUsername");
        when(userRepository.findByUsername("testUsername")).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName("testUsername");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        assertNotNull(response.getBody());
        assertEquals(user, response.getBody());
    }

    @Test
    public void find_by_id_happy_path(){
        User user = new User();
        user.setId(0L);
        user.setUsername("testUsername");
        when(userRepository.findById(0L)).thenReturn(Optional.of(user));

        final ResponseEntity<User> response = userController.findById(0L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        assertNotNull(response.getBody());
        assertEquals(user, response.getBody());
    }

    @Test
    public void find_by_username_unhappy_path(){

//        search for non-existent user
        final ResponseEntity responseFind1 = userController.findByUserName("testUsername");
        assertNotNull(responseFind1);
        assertEquals(404, responseFind1.getStatusCodeValue());

//        set password less than 5 characters
        User user = new User();
        user.setUsername("testUsername");
        user.setPassword("tp");

        final ResponseEntity<User> response2 = userController.findByUserName("testUsername");
        assertNotNull(response2);
        assertEquals(404, response2.getStatusCodeValue());

    }
}
