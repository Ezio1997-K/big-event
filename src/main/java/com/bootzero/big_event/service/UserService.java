package com.bootzero.big_event.service;

import com.bootzero.big_event.bean.User;
import org.springframework.stereotype.Service;

/**
 * ClassName: UserService
 * Package: com.bootzero.big_event.service.impl
 * Description:
 *
 */

public interface UserService {
    User findUserByName(String username);

    void register(String username, String password);
    //更新
    void update(User user);
    //更新头像
    void updateAvatar(String avatarUrl);

    void updatePwd(String newPwd);
}
