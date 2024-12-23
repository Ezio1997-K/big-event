package com.bootzero.big_event.service.impl;

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
}
