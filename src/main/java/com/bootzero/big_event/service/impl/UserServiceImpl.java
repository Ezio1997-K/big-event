package com.bootzero.big_event.service.impl;

import com.bootzero.big_event.bean.User;
import com.bootzero.big_event.mapper.UserMapper;
import com.bootzero.big_event.service.UserService;
import com.bootzero.big_event.utils.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ClassName: UserServiceImpl
 * Package: com.bootzero.big_event.service.impl.impl
 * Description:
 *
 */
@RequiredArgsConstructor
@Service("userService")
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    @Override
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    @Override
    public void register(String username, String password) {
        String md5String = DigestUtils.md5DigestAsHex(password.getBytes());
        userMapper.add(username,md5String);
    }

    @Override
    public void update(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }

    @Override
    public void updateAvatar(String avatarUrl) {
        Map<String, Object> userData = ThreadLocalUtil.get();
        Integer id = (Integer) userData.get("id");
        userMapper.updateAvatar(avatarUrl,id);
    }

    @Override
    public void updatePwd(String newPwd) {
        Map<String, Object> userData = ThreadLocalUtil.get();
        Integer id = (Integer) userData.get("id");
        userMapper.updatePwd(DigestUtils.md5DigestAsHex(newPwd.getBytes()),id);
    }
}
