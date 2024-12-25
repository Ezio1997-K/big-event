package com.bootzero.big_event;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class BigEventApplicationTests {

    @Test
    void contextLoads() {
    }
    //测试Jwt
    @Test
    public void testJwt(){
        Map<String,Object> clains = new HashMap<>();
        clains.put("id",112233);
        clains.put("name","张三");
        String token = JWT.create().withClaim("user", clains)
                .withExpiresAt(new Date(System.currentTimeMillis() * 1000 * 60))
                .sign(Algorithm.HMAC256("123457"));
        System.out.println(token);
    }

}
