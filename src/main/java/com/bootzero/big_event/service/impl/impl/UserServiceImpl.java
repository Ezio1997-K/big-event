package com.bootzero.big_event.service.impl.impl;

import com.bootzero.big_event.bean.User;
import com.bootzero.big_event.mapper.UserMapper;
import com.bootzero.big_event.service.impl.UserService;
import org.springframework.stereotype.Service;

/**
 * ClassName: UserServiceImpl
 * Package: com.bootzero.big_event.service.impl.impl
 * Description:
 *
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    private UserMapper userMapper;
    @Override
    public User findUserByName(String username) {
        return userMapper.selectByName;
    }

    @Override
    public void register(String username, String password) {
        User user = new User(Integer.parseInt(username),password);
        userMapper.insert(user);
    }
}
