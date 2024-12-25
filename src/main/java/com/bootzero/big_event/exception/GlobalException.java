package com.bootzero.big_event.exception;

import com.bootzero.big_event.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * ClassName: GlobalException
 * Package: com.bootzero.big_event.exception
 * Description:
 *
 */
@Slf4j
@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e){
        log.error(StringUtils.hasLength(e.getMessage())?e.getMessage():"操作失败！！");
        return Result.error((StringUtils.hasLength(e.getMessage()))?e.getMessage():"操作失败");

    }
}
