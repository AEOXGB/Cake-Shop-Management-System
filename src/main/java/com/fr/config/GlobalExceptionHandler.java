package com.fr.config;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 
 * 该类使用 @RestControllerAdvice 注解，实现全局异常处理功能。
 * 作用：统一捕获和处理 Controller 层抛出的异常，避免异常直接暴露给前端。
 * 目前处理的异常类型：
 * 1. MethodArgumentNotValidException - 参数校验失败异常
 *
 * @author CakeShop Team
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验失败异常
     * 
     * 当使用 @Valid 或 @Validated 注解进行参数校验失败时，
     * 会抛出 MethodArgumentNotValidException 异常，由此方法进行处理。
     * 将校验错误信息整理成 Map 格式返回给前端，包含错误码、提示信息和详细错误字段。
     *
     * @param e 参数校验失败异常对象，包含校验错误的详细信息
     * @return Map&lt;String, Object&gt; 返回包含错误信息的 Map 集合
     *         - code: 错误码，400 表示参数错误
     *         - msg: 错误提示信息
     *         - data: 详细的字段错误信息，key 为字段名，value 为错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidException(MethodArgumentNotValidException e) {
        // 创建结果 Map，用于存放返回数据
        Map<String, Object> map = new HashMap<>();
        // 设置错误码为 400，表示客户端请求参数错误
        map.put("code", 400);
        // 设置错误提示信息
        map.put("msg", "参数校验失败");

        // 创建错误详情 Map，用于存放各个字段的具体错误信息
        Map<String, String> errorMap = new HashMap<>();
        // 遍历所有校验错误，提取字段名和错误信息
        e.getBindingResult().getAllErrors().forEach(error -> {
            // 强转为 FieldError，获取字段名称
            String fieldName = ((FieldError) error).getField();
            // 获取错误提示信息
            String message = error.getDefaultMessage();
            // 将字段名和错误信息存入错误详情 Map
            errorMap.put(fieldName, message);
        });

        // 将详细错误信息放入返回结果中
        map.put("data", errorMap);
        // 返回处理结果
        return map;
    }
}
