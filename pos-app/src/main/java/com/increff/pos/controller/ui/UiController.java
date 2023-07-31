package com.increff.pos.controller.ui;


import com.increff.pos.controller.ui.AbstractUiController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UiController extends AbstractUiController {

    @RequestMapping(value = "/ui/home")
    public ModelAndView home() {
        return mav("home.html");
    }

    @RequestMapping(value = "/site/login")
    public ModelAndView login() {
        return mav("login.html");
    }

    @RequestMapping(value = "/site/signup")
    public ModelAndView signup() {
        return mav("signup.html");
    }

    @RequestMapping(value = "/ui/brands")
    public ModelAndView brands() {
        return mav("brands.html");
    }

    @RequestMapping(value = "/ui/products")
    public ModelAndView products() {
        return mav("products.html");
    }

    @RequestMapping(value = "/ui/inventory")
    public ModelAndView inventory() {
        return mav("inventory.html");
    }

    @RequestMapping(value = "/ui/orders")
    public ModelAndView orders() {
        return mav("orders.html");
    }

    @RequestMapping(value = "/ui/reports")
    public ModelAndView reports() {
        return mav("reports.html");
    }

}
