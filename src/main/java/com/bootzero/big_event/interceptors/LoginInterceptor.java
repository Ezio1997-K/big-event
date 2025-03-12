package com.bootzero.big_event.interceptors;

import com.bootzero.big_event.utils.JwtUtil;
import com.bootzero.big_event.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * ClassName: LoginInterceptor
 * Package: com.bootzero.big_event.interceptors
 * Description:
 *
 */
@RequiredArgsConstructor
@Component
public class LoginInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        try{
            String token = request.getHeader("Authorization");
            if (token == null) {
                throw new RuntimeException();
            }

            Map<String, Object> userData = JwtUtil.parseToken(token);
            //检查redis中是否有一样的token
            String redisToken = stringRedisTemplate.opsForValue().get(userData.get("username").toString());
            if (redisToken == null || !redisToken.equals(token)) {
                throw new RuntimeException();
            }
            ThreadLocalUtil.set(userData);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtil.remove();
    }
}
