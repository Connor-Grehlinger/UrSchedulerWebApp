package edu.TeamAlpha.meetingManager.controllers;

import edu.TeamAlpha.meetingManager.models.*;
import edu.TeamAlpha.meetingManager.Service.*;

import javax.servlet.ServletException;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @Autowired
    EmailService emailService;
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {

        this.userService = userService;
    }
    //
    // @RequestMapping(value = "/perform_login", method = RequestMethod.POST)
    // public String getLogin(@RequestParam("username") String name, @RequestParam("password") String password) throws Exception {
    //     System.out.println("In login post");
    //     System.out.println("name: " + name + " pass:" + password);
    //     if(this.userService.checkLoginCred(name, password)){
    //       return "redirect:/home";
    //     }
    //     return "/login";
    // }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String save(@ModelAttribute(value = "signupinfo") CustomUserDetails userDetails) {
        try {
            this.userService.save(userDetails);
            Mail mail = new Mail();
            //mail.setFrom("leidongyao@gmail.com");
            mail.setTo(userDetails.getEmail());
            mail.setSubject("Registration Confirm");
            mail.setContent("Welcome to UrScheduler!");
            emailService.sendSimpleMessage(mail);
            System.out.println("signup succeeded!!");
            return "/login";
        } catch (Exception e) {
            System.out.println("signup failed!!");
            return "/signupFailure";
        }
        // return "forward:/login";
    }
    @RequestMapping(value = "/signupFailure")
    public String signupFailure() {
        return "signupFailure";
    }
}
