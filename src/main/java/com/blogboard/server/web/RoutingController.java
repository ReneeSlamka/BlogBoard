package com.blogboard.server.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class RoutingController {

    @RequestMapping(value ="/", method= RequestMethod.GET)
    public ModelAndView getIndexPage() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("index");
        return mav;
    }

    @RequestMapping(value ="/login", method= RequestMethod.GET)
    public ModelAndView getLoginPage() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("login");
        return mav;
    }




}
