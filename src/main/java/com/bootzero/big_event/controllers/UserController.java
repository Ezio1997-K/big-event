package com.bootzero.big_event.controllers;

import com.bootzero.big_event.bean.Result;
import com.bootzero.big_event.bean.User;
import com.bootzero.big_event.service.UserService;
import com.bootzero.big_event.utils.JwtUtil;
import com.bootzero.big_event.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private final StringRedisTemplate stringRedisTemplate;
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
            //将token往redis中也存一份
            stringRedisTemplate.opsForValue().set(username,token,1, TimeUnit.HOURS);
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
    @PutMapping("/update")
    public Result<Void> updateUserInfo(@RequestBody @Validated User user){
        userService.update(user);
        return Result.success();
    }
    @PatchMapping("/updateAvatar")
    public Result<Void> updateAvatar(@RequestParam @URL String avatarUrl){
        userService.updateAvatar(avatarUrl);
        return Result.success();
    }

    @PatchMapping("/updatePwd")
    public Result<Void> updatePwd(@RequestBody Map<String, String> params,@RequestHeader("Authorization") String token) {
        //1.校验参数
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");

        if (!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)) {
            return Result.error("缺少必要的参数");
        }

        if (!rePwd.equals(newPwd)) {
            return Result.<Void>error("两次密码不一致");
        }

        //原密码是否正确
        //调用userService根据用户名拿到原密码,再和old_pwd比对
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User loginUser = userService.findUserByName(username);
        if (!loginUser.getPassword().equals(DigestUtils.md5DigestAsHex(oldPwd.getBytes()))) {
            return Result.<Void>error("原密码输入错误");
        }
        //2.调用service完成密码更新
        userService.updatePwd(newPwd);
        //3.删除redis中旧令牌
        //ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        //stringStringValueOperations.getOperations().delete("token");
        stringRedisTemplate.delete(username);
        return Result.success();
    }
    @GetMapping("/logout")
    public Result<Void> logout(){
        //获取用户名
        Map<String,Object> map =  ThreadLocalUtil.get();
        String username = (String) map.get("username");
        //删除redis令牌
        stringRedisTemplate.delete(username);
        return Result.success();
    }
}
