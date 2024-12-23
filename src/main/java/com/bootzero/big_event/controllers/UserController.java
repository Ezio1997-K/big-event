package com.bootzero.big_event.controllers;

import com.bootzero.big_event.bean.Result;
import com.bootzero.big_event.bean.User;
import com.bootzero.big_event.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * ClassName: UserController
 * Package: com.bootzero.big_event.controllers
 * Description:
 */
@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public Result register(String username, String password) {
        //判断用户名是否已经存在
        User user = userService.findUserByName(username);
        if (user != null) {
            userService.register(username,password);
            return Result.success();
        }else {
            return Result.error("用户名已经存在");
        }
    }
}
