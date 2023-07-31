package com.increff.pos.service;

import com.increff.pos.dao.UserDao;
import com.increff.pos.model.enums.UserRole;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.AbstractUnitTest;
import com.increff.pos.util.ApiTestUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class UserServiceTest extends AbstractUnitTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ApiTestUtil apiTestUtil;

    @Autowired
    private UserDao userDao;

    // Tests adding new user
    @Test
    public void testAdd1() throws ApiException {
        userService.add("temp@email.com", "123");

        List<UserPojo> userPojoList = userDao.getAll();
        assertEquals(1, userPojoList.size());
        assertEquals("temp@email.com", userPojoList.get(0).getEmail());
        assertEquals("123", userPojoList.get(0).getPassword());
    }

    // Tests getting user by email
    @Test
    public void testGet1() {
        String email = "email@email.com";
        UserPojo savedUserPojo = apiTestUtil.addUser(email, "123", UserRole.OPERATOR);

        UserPojo userPojo = userService.findByEmail(email);
        assertEquals(savedUserPojo, userPojo);
    }

}
