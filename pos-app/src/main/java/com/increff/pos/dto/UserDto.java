package com.increff.pos.dto;

import com.increff.pos.model.data.InfoData;
import com.increff.pos.model.form.UserLoginForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.exception.ApiException;
import com.increff.pos.service.UserService;
import com.increff.pos.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;

@Component
public class UserDto {

    @Autowired
    private InfoData info;

    @Autowired
    private UserService userService;

    public ModelAndView loginUser(HttpServletRequest req, UserLoginForm userLoginForm) throws ApiException {
        NormalizeUtil.normalize(userLoginForm);
        ValidateUtil.validateForm(userLoginForm);

        UserPojo userPojo = userService.findByEmail(userLoginForm.getEmail());

        boolean authenticated = (userPojo != null && Objects.equals(userPojo.getPassword(), userLoginForm.getPassword()));

        if (!authenticated) {
            throw new ApiException("Invalid user credentials");
        }

        Authentication authentication = AuthenticationUtil.convert(userPojo);
        HttpSession session = req.getSession(true);
        SecurityUtil.createContext(session);
        SecurityUtil.setAuthentication(authentication);

        return new ModelAndView("redirect:/ui/home");
    }

    public ModelAndView signupUser(HttpServletRequest request, UserLoginForm userLoginForm) throws ApiException {
        NormalizeUtil.normalize(userLoginForm);
        ValidateUtil.validateForm(userLoginForm);

        UserPojo userPojo = userService.add(userLoginForm.getEmail(), userLoginForm.getPassword());

        Authentication authentication = AuthenticationUtil.convert(userPojo);
        HttpSession session = request.getSession(true);
        SecurityUtil.createContext(session);
        SecurityUtil.setAuthentication(authentication);

        return new ModelAndView("redirect:/ui/home");
    }

    public ModelAndView logoutUser(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();

        return new ModelAndView("redirect:/site/login");
    }

}
