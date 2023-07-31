package com.increff.pos.controller.ui;

import com.increff.pos.model.data.InfoData;
import com.increff.pos.util.AuthenticationUtil;
import com.increff.pos.util.SecurityUtil;
import com.increff.pos.util.UserPrincipal;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

@Controller
@Log4j
public abstract class AbstractUiController {

    @Autowired
    private InfoData info;

    @Value("${app.baseUrl}")
    private String baseUrl;

    protected ModelAndView mav(String page) {
        // Get current user
        UserPrincipal principal = SecurityUtil.getPrincipal();

        info.setShown(principal != null);
        info.setEmail(principal == null ? "" : principal.getEmail());
        info.setRole(principal == null? "" : Objects.requireNonNull(AuthenticationUtil.getUserRole()).name());

        // Set info
        ModelAndView mav = new ModelAndView(page);
        mav.addObject("info", info);
        mav.addObject("baseUrl", baseUrl);
        return mav;
    }

}
