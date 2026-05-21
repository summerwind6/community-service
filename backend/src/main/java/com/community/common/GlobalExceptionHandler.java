package com.community.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.UnknownSessionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== 业务异常 ====================

    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    // ==================== Shiro 认证异常 ====================

    @ExceptionHandler(UnknownAccountException.class)
    public Result<String> handleUnknownAccountException(UnknownAccountException e) {
        log.warn("认证异常-用户不存在：{}", e.getMessage());
        return Result.error(401, "用户不存在");
    }

    @ExceptionHandler(LockedAccountException.class)
    public Result<String> handleLockedAccountException(LockedAccountException e) {
        log.warn("认证异常-账号被禁用：{}", e.getMessage());
        return Result.error(403, "账号已被禁用，请联系管理员");
    }

    @ExceptionHandler(AuthenticationException.class)
    public Result<String> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证异常：{}", e.getMessage());
        return Result.error(401, "用户名或密码错误");
    }

    // ==================== Shiro 授权异常 ====================

    @ExceptionHandler(UnauthorizedException.class)
    public Result<String> handleUnauthorizedException(UnauthorizedException e) {
        log.warn("授权异常：{}", e.getMessage());
        return Result.error(403, "没有权限访问该资源");
    }

    // ==================== 参数校验异常 ====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null ?
                e.getBindingResult().getFieldError().getDefaultMessage() : "参数校验失败";
        log.warn("参数校验异常：{}", message);
        return Result.error(400, message);
    }

    @ExceptionHandler(BindException.class)
    public Result<String> handleBindException(BindException e) {
        String message = e.getFieldError() != null ?
                e.getFieldError().getDefaultMessage() : "参数绑定失败";
        log.warn("参数绑定异常：{}", message);
        return Result.error(400, message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<String> handleMissingParamException(MissingServletRequestParameterException e) {
        log.warn("缺少请求参数：{}", e.getParameterName());
        return Result.error(400, "缺少必要参数: " + e.getParameterName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<String> handleMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("请求体格式错误：{}", e.getMessage());
        return Result.error(400, "请求参数格式错误");
    }

    // ==================== Shiro 会话异常 ====================

    @ExceptionHandler(UnknownSessionException.class)
    public Result<String> handleUnknownSessionException(UnknownSessionException e) {
        log.warn("会话异常-会话不存在：{}", e.getMessage());
        return Result.error(401, "会话已失效，请重新登录");
    }

    @ExceptionHandler(ExpiredSessionException.class)
    public Result<String> handleExpiredSessionException(ExpiredSessionException e) {
        log.warn("会话异常-会话已过期：{}", e.getMessage());
        return Result.error(401, "会话已过期，请重新登录");
    }

    // ==================== 兜底异常 ====================

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.error("系统繁忙，请稍后再试");
    }
}
