package com.increff.pos.service;

import javax.transaction.Transactional;

import com.increff.pos.dao.UserDao;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.pojo.UserPojo;

@Service
@Transactional
public class UserService extends AbstractService {

    @Autowired
    private UserDao userDao;

    public UserPojo add(String email, String password) throws ApiException {
        UserPojo existingUserPojo = findByEmail(email);

        checkNull(existingUserPojo, "You have already registered with this email");

        UserPojo userPojo = createNewUser(email, password);

        return userDao.save(userPojo);
    }

    public UserPojo findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    private UserPojo createNewUser(String email, String password) {
        UserPojo userPojo = new UserPojo();

        userPojo.setEmail(email);
        userPojo.setPassword(password);

        setUserRole(userPojo);

        return userPojo;
    }

    private void setUserRole(UserPojo userPojo) {
        userPojo.setRole(
                AuthenticationUtil.assignRoleByEmail(userPojo.getEmail())
        );
    }

}