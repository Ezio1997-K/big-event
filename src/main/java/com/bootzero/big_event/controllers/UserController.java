package com.bootzero.big_event.controllers;

import com.bootzero.big_event.bean.Result;
import com.bootzero.big_event.bean.User;
import com.bootzero.big_event.service.UserService;
import com.bootzero.big_event.utils.JwtUtil;
import com.bootzero.big_event.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: UserController
 * Package: com.bootzero.big_event.controllers
 * Description:
 */
@RestController
@RequestMapping("/user")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/register")
    public Result register(@Pattern(regexp = "\\S{5,16}") String username,@Pattern(regexp = "\\S{5,16}") String password) {
        //判断用户名是否已经存在
        User user = userService.findUserByName(username);
        if (user == null) {
            userService.register(username,password);
            return Result.<Void>success();
        }else {
            return Result.<Void>error("用户名已经存在");
        }
    }

    @PostMapping("/login")
    public Result<?> login(@Pattern(regexp = "\\S{5,16}") String username,@Pattern(regexp = "\\S{5,16}") String password) {
        //判断用户是否已经存在
        User user = userService.findUserByName(username);
        if (user == null) {
            return Result.<Void>error("该用户不存在！");
        }

        if(DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())){
            Map<String,Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            claims.put("username", user.getUsername());
            String token = JwtUtil.genToken(claims);
            return Result.success(token);
        }else {
            return Result.<Void>error("密码错误");
        }
    }
    @GetMapping("/userInfo")
    public Result<User> userInfo(){
        Map<String,Object> userData = ThreadLocalUtil.get();
        String username = (String) userData.get("username");
        User user = userService.findUserByName(username);
        return Result.success(user);
    }
}
