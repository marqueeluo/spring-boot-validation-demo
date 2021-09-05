package com.luo.demo.validation.controller;

import com.luo.demo.validation.domain.result.CommonResult;
import com.luo.demo.validation.enums.RespCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * controller增强 - 通用异常处理
 *
 * @author luohq
 * @date 2021-09-04 13:43
 */
@ControllerAdvice
public class ControllerAdviceHandler {

    private static final Logger log = LoggerFactory.getLogger(ControllerAdviceHandler.class);

    /**
     * 是否在响应结果中展示验证错误提示信息
     */
    @Value("${spring.validation.msg.enable:true}")
    private Boolean enableValidationMsg;

    /**
     * 符号常量
     */
    private final String DOT = ".";
    private final String SEPARATOR_COMMA = ", ";
    private final String SEPARATOR_COLON = ": ";

    /**
     * 验证异常处理 - 在@RequestBody上添加@Validated处触发
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CommonResult handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException ex) {
        log.warn("{} - MethodArgumentNotValidException!", request.getServletPath());
        CommonResult commonResult = CommonResult.respWith(RespCodeEnum.PARAM_INVALID.getCode(), this.convertFiledErrors(ex.getBindingResult().getFieldErrors()));
        log.warn("{} - resp with param invalid: {}", request.getServletPath(), commonResult);
        return commonResult;
    }

    /**
     * 验证异常处理 - form参数（对象参数，没有加@RequestBody）触发
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler({BindException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CommonResult handleBindException(HttpServletRequest request, BindException ex) {
        log.warn("{} - BindException!", request.getServletPath());
        CommonResult commonResult = CommonResult.respWith(RespCodeEnum.PARAM_INVALID.getCode(), this.convertFiledErrors(ex.getFieldErrors()));
        log.warn("{} - resp with param invalid: {}", request.getServletPath(), commonResult);
        return commonResult;
    }


    /**
     * 验证异常处理 - @Validated加在controller类上，
     * 且在参数列表中直接指定constraints时触发
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CommonResult handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException ex) {
        log.warn("{} - ConstraintViolationException - {}", request.getServletPath(), ex.getMessage());
        CommonResult commonResult = CommonResult.respWith(RespCodeEnum.PARAM_INVALID.getCode(), this.convertConstraintViolations(ex));
        log.warn("{} - resp with param invalid: {}", request.getServletPath(), commonResult);
        return commonResult;
    }

    /**
     * 全局默认异常处理
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler({Throwable.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CommonResult handleException(HttpServletRequest request, Throwable ex) {
        log.warn("{} - Exception!", request.getServletPath(), ex);
        CommonResult commonResult = CommonResult.failed();
        log.warn("{} - resp failed: {}", request.getServletPath(), commonResult);
        return commonResult;
    }

    /**
     * 转换FieldError列表为错误提示信息
     *
     * @param fieldErrors
     * @return
     */
    private String convertFiledErrors(List<FieldError> fieldErrors) {
        return Optional.ofNullable(fieldErrors)
                .filter(fieldErrorsInner -> this.enableValidationMsg)
                .map(fieldErrorsInner -> fieldErrorsInner.stream()
                        .flatMap(fieldError -> Stream.of(fieldError.getField(), SEPARATOR_COLON, fieldError.getDefaultMessage(), SEPARATOR_COMMA))
                        .collect(Collectors.joining()))
                .map(msg -> msg.substring(0, msg.length() - SEPARATOR_COMMA.length()))
                .orElse(null);
    }

    /**
     * 转换ConstraintViolationException异常为错误提示信息
     *
     * @param constraintViolationException
     * @return
     */
    private String convertConstraintViolations(ConstraintViolationException constraintViolationException) {
        return Optional.ofNullable(constraintViolationException.getConstraintViolations())
                .filter(constraintViolations -> this.enableValidationMsg)
                .map(constraintViolations -> constraintViolations.stream()
                        .flatMap(constraintViolation -> {
                            String path = constraintViolation.getPropertyPath().toString();
                            path = path.substring(path.lastIndexOf(DOT) + 1);
                            String errMsg = constraintViolation.getMessage();
                            return Stream.of(path, SEPARATOR_COLON, errMsg, SEPARATOR_COMMA);
                        }).collect(Collectors.joining())
                ).map(msg -> msg.substring(0, msg.length() - SEPARATOR_COMMA.length()))
                .orElse(null);

    }
}