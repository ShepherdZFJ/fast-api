package com.shepherd.fast.global;

import com.alibaba.fastjson.JSON;
import com.shepherd.fast.enums.ResponseStatusEnum;
import com.shepherd.fast.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2022/6/16 10:45
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 全局异常处理
     * @param e
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public ResponseVO exceptionHandler(Exception e){
        // 处理业务异常
        if (e instanceof BizException) {
            BizException bizException = (BizException) e;
            if (bizException.getCode() == null) {
                bizException.setCode(ResponseStatusEnum.BAD_REQUEST.getCode());
            }
            return ResponseVO.failure(bizException.getCode(), bizException.getMessage());
        } else if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) e;
            Map<String, String> map = new HashMap<>();
            BindingResult result = methodArgumentNotValidException.getBindingResult();
            result.getFieldErrors().forEach((item)->{
                String message = item.getDefaultMessage();
                String field = item.getField();
                map.put(field, message);
            });
            log.error("数据校验出现问题:{},异常类型{}", methodArgumentNotValidException.getMessage(), methodArgumentNotValidException.getClass());
            return ResponseVO.failure(ResponseStatusEnum.BAD_REQUEST, map);
        } else {
            //如果是系统的异常，比如空指针这些异常
            log.error("【系统异常】", e);
            return ResponseVO.failure(ResponseStatusEnum.SYSTEM_ERROR.getCode(), ResponseStatusEnum.SYSTEM_ERROR.getMsg());
        }
    }

}
