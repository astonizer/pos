package com.increff.pos.controller.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.increff.pos.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.increff.pos.model.form.UserLoginForm;
import com.increff.pos.service.exception.ApiException;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/api/session")
public class AuthController {

    @Autowired
    private UserDto userDto;

    @ApiOperation(value = "Logs in a user")
    @PostMapping(path = "/login")
    public ModelAndView loginUser(HttpServletRequest request, @RequestBody UserLoginForm userLoginForm) throws ApiException {
        return userDto.loginUser(request, userLoginForm);
    }

    @ApiOperation(value = "Signups a user")
    @PostMapping(path = "/signup")
    public ModelAndView signupUser(HttpServletRequest request, @RequestBody UserLoginForm userLoginForm) throws ApiException {
        return userDto.signupUser(request, userLoginForm);
    }

    @ApiOperation(value = "Logs out a user")
    @GetMapping("/logout")
    public ModelAndView logoutUser(HttpServletRequest request, HttpServletResponse response) {
        return userDto.logoutUser(request, response);
    }

}