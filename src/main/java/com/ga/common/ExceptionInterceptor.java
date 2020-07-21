package com.ga.common;

import com.ga.entity.model.BaseResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zelei.fan
 * @date 2019/12/18 17:20
 * @description 异常拦截器
 */
@ControllerAdvice
@ResponseBody
public class ExceptionInterceptor {

    @ExceptionHandler(ServiceException.class)
    public BaseResponse myException(ServiceException e){
        return new BaseResponse(e.getCode(), e.getMsg());
    }

    /*@ExceptionHandler(Exception.class)
    public BaseModel consoleException(Exception e){
        e.printStackTrace();
        return new BaseModel(ResultCode.ERROR, e.getMessage());
    }*/

    /**
     * 参数校验异常
     * @param e
     * @return
     */
    @ExceptionHandler(BindException.class)
    public BaseResponse validatorException(BindException e){
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult.hasErrors()){
            FieldError fieldError = bindingResult.getFieldError();
            return new BaseResponse(201, fieldError.getDefaultMessage());
        }
        return new BaseResponse(201, e.getCause().getMessage());
    }
}
