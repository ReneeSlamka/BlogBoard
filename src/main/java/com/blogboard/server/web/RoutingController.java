package com.blogboard.server.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class RoutingController {

    /*@RequestMapping(value ="/", method= RequestMethod.GET)
    String index() {
        return "index";
    }*/

    @RequestMapping(value ="/", method= RequestMethod.GET)
    public ModelAndView getIndexPage() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("index");
        return mav;
    }

    /*@RequestMapping(value ="/login", method= RequestMethod.GET)
    String login() {
        return "login";
    }*/

    @RequestMapping(value ="/login", method= RequestMethod.GET)
    public ModelAndView getLoginPage() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("login");
        return mav;
    }

    @RequestMapping(value ="/home", method= RequestMethod.GET)
    public ModelAndView getHomePage() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("home");
        return mav;
    }

    @RequestMapping(value ="/test", method= RequestMethod.GET)
    public ModelAndView getTest() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("test");
        return mav;
    }
}
