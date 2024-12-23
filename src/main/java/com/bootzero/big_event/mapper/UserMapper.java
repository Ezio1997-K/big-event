package com.bootzero.big_event.mapper;

import com.bootzero.big_event.bean.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: UserMapper
 * Package: com.bootzero.big_event.mapper
 * Description:
 *
 */
@Mapper
public interface UserMapper {
    //根据用户名查询
    User selectByName(String username);

    //添加用户
    void add(String username, String password);

}
