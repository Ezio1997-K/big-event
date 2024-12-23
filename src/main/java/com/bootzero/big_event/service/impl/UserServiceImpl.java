package com.bootzero.big_event.service.impl;

import com.bootzero.big_event.bean.User;
import com.bootzero.big_event.mapper.UserMapper;
import com.bootzero.big_event.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * ClassName: UserServiceImpl
 * Package: com.bootzero.big_event.service.impl.impl
 * Description:
 *
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    @Override
    public void register(String username, String password) {
        String md5String = DigestUtils.md5DigestAsHex(password.getBytes());
        userMapper.add(username,md5String);
    }
}
