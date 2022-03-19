package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private final UserRepository userRepository = mock(UserRepository.class);

    private final CartRepository cartRepository = mock(CartRepository.class);

    private final BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);

    @Before
    public void setUp(){

        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    @Order(1)
    public void create_user_happy_path() {
        log.info("Testing creation of a new user");
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
        log.info("User has been created successfully");
    }

    @Test
    @Order(2)
    public void find_by_user_name_happy_path(){
        log.info("testing finding user by username (happy path)");
        User user = createUser();
        when(userRepository.findByUsername("testUsername")).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName("testUsername");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        assertNotNull(response.getBody());
        assertEquals(user, response.getBody());
        log.info("User with username testUsername has been found");
    }

    @Test
    @Order(3)
    public void find_by_id_happy_path(){
        log.info("testing finding user by user id (happy path)");
        User user = createUser();
        when(userRepository.findById(0L)).thenReturn(Optional.of(user));

        final ResponseEntity<User> response = userController.findById(0L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        assertNotNull(response.getBody());
        assertEquals(user, response.getBody());
        log.info("User with user id 0 has been found");
    }

    @Test
    @Order(4)
    public void create_user_unhappy_path(){
        log.info("Testing creation of a new user (unhappy path)");
        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testUsername");
//        set password less than 5 characters
        request.setPassword("tp");
        request.setConfirmPassword("tp");

        final ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        log.warn("User could not be created");
    }

    @Test
    @Order(5)
    public void find_by_username_unhappy_path(){
        log.info("testing finding user by username (unhappy path, i.e. for non-existent username)");
//        search for non-existent username
        final ResponseEntity<User> responseFind = userController.findByUserName("testUsername");
        assertNotNull(responseFind);
        assertEquals(404, responseFind.getStatusCodeValue());
        log.info("User with username testUsername does not exist");

    }

    @Test
    @Order(6)
    public void find_by_id_unhappy_path(){
        log.info("testing finding user by user id (unhappy path, i.e. for non-existent user id)");
        //        search for non-existent user id
        final ResponseEntity<User> responseFind = userController.findById(1L);
        assertNotNull(responseFind);
        assertEquals(404, responseFind.getStatusCodeValue());
        log.info("User with user id 1 does not exist");

    }

//    helper methods
    public User createUser(){
        User user = new User();
        user.setId(0L);
        user.setUsername("testUsername");
        user.setPassword("password");

        return user;
    }
}
