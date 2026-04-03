package org.faicai.d4c.exception;

import lombok.extern.slf4j.Slf4j;
import org.faicai.d4c.constant.ResponseCode;
import org.faicai.d4c.utils.R;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

/**
 * @author yeshen.lan
 */
@RestControllerAdvice
@Slf4j
public class BaseExceptionHandler {
    public static final String LEFT_BRACE = "{";
    public static final String RIGHT_BRACE = "}";
    public static final int EXCEPTION_MSG_LENGTH = 800;
    protected MessageSource messageSource;

    public BaseExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * 处理所有不可知的异常
     */
    @ExceptionHandler(Exception.class)
    public R<?> exception(Exception e) {
        log.error(e.getMessage(), e);
        return R.failed(ResponseCode.SYSTEM_EXCEPTION).setMsg(getMessage(ResponseCode.SYSTEM_EXCEPTION)).setDeveloperMsg(BusinessException.getStackTrace(e));
    }

    @ExceptionHandler(BindException.class)
    public R<?> exception(BindException e) {
        log.error(e.getMessage(), e);
        Object[] objects = e.getBindingResult().getAllErrors().stream().map((Function<ObjectError, Object>) DefaultMessageSourceResolvable::getDefaultMessage).toArray();
        return R.failed(ResponseCode.REQUEST_VALUE_MISSING).setMsg(StringUtils.arrayToDelimitedString(objects, ",")).setDeveloperMsg(BusinessException.getStackTrace(e));
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<?> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);

        return R.failed(ResponseCode.VALUE_MISSING).setMsg(getMessage(ResponseCode.VALUE_MISSING)).setDeveloperMsg(BusinessException.getStackTrace(e));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R<?> missingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        String message = e.getParameterName() + ": " + getMessage(ResponseCode.VALUE_MISSING);
        return R.failed(ResponseCode.VALUE_MISSING).setMsg(message).setDeveloperMsg(BusinessException.getStackTrace(e));
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public R<?> missingPathVariableException(MissingPathVariableException e) {
        log.error(e.getMessage(), e);
        String message = e.getVariableName() + ": " + getMessage(ResponseCode.VALUE_MISSING);
        return R.failed(ResponseCode.VALUE_MISSING).setMsg(message).setDeveloperMsg(BusinessException.getStackTrace(e));
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public R<?> servletRequestBindingException(ServletRequestBindingException e) {
        log.error(e.getMessage(), e);
        String message = e.getMessage();
        if (StringUtils.hasText(message)) {
            return R.failed(ResponseCode.VALUE_MISSING).setMsg(getMessage(ResponseCode.VALUE_MISSING)).setDeveloperMsg(BusinessException.getStackTrace(e));
        }
        int first = message.indexOf("'");
        int second = message.indexOf("'", first + 1);
        String name = message.substring(first + 1, second);
        String userMessage = name + ": " + getMessage(ResponseCode.VALUE_MISSING);
        return R.failed(ResponseCode.VALUE_MISSING).setMsg(userMessage).setDeveloperMsg(BusinessException.getStackTrace(e));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<?> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return R.failed(ResponseCode.SYSTEM_EXCEPTION).setMsg(getMessage(ResponseCode.SYSTEM_EXCEPTION)).setDeveloperMsg(BusinessException.getStackTrace(e));
    }

    @ExceptionHandler(BusinessException.class)
    public R<?> businessException(BusinessException e) {
        return R.failed(e.getCode()).setMsg(getMessage(e.getCode(), e.getArgs()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        // log.error(e.getMessage(), e);
        BindingResult bindingResult = e.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        Map<String, Set<String>> errors = new HashMap<>();
        allErrors.forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorCode = error.getDefaultMessage();
            // 获取国际化消息
            String errorMessage;
            // 尝试从消息代码解析消息
            if (errorCode != null && errorCode.startsWith("{") && errorCode.endsWith("}")) {
                String code = errorCode.substring(1, errorCode.length() - 1);
                errorMessage = getMessage(code);
            } else {
                errorMessage = errorCode;
            }
            if (!"".equals(errorMessage)) {
                Set<String> orDefault = errors.getOrDefault(fieldName, new HashSet<>());
                orDefault.add(errorMessage);
                errors.put(fieldName, orDefault);
            }
        });
        return R.failed(ResponseCode.VALUE_VALIDATION_FAILED).setMsg(getMessage(ResponseCode.VALUE_VALIDATION_FAILED)).setData(errors);
    }

    @ExceptionHandler(BaseException.class)
    public R<?> baseExceptionHandler(BaseException e) {
        String message = e.getUserMsg();
        if (message == null || message.isEmpty()) {
            message = getMessage(e.getCode(), e.getArgs());
        } else if (message.startsWith(LEFT_BRACE) && message.endsWith(RIGHT_BRACE)) {
            message = getMessage(message.substring(1, message.length() - 1), e.getArgs());
        }
        e.setUserMsg(message);
        log.error(e.getMessage(), e);
        return R.failed(e.getCode()).setMsg(getMessage(e.getCode()));
    }

    @ExceptionHandler(SQLException.class)
    public R<?> sqlExceptionHandler(SQLException e) {
        log.error("SQL异常", e);
        return R.failed(ResponseCode.SYSTEM_EXCEPTION, "SQL Exception", BusinessException.getStackTrace(e));
    }


    /**
     * 把值绑定到Model中，使全局@RequestMapping可以获取到该值
     *
     * @param model model
     */
    @ModelAttribute
    public void addAttributes(Model model) {
    }

    /**
     * 获取校验的提示信息
     *
     * @param allErrors 所有错误信息
     * @return String
     */
    protected String getValidMessage(List<ObjectError> allErrors) {
        if (CollectionUtils.isEmpty(allErrors)) {
            return null;
        }
        String field = null;
        StringBuilder errorMsg = new StringBuilder();
        ObjectError error = allErrors.get(0);
        if (error instanceof FieldError) {
            field = ((FieldError) error).getField();
        }
        if (StringUtils.hasLength(field)) {
            errorMsg.append(field);
            errorMsg.append(":");
        }
        String defaultMessage = error.getDefaultMessage();
        if (defaultMessage == null) {
            return errorMsg.append(" validate failure !").toString();
        }
        if (defaultMessage.startsWith(LEFT_BRACE) && defaultMessage.endsWith(RIGHT_BRACE)) {
            errorMsg.append(getMessage(defaultMessage.substring(1, defaultMessage.length() - 1)));
        } else {
            errorMsg.append(defaultMessage);
        }
        return errorMsg.toString();
    }

    /**
     * 根据错误码返回国际化错误信息
     */
    public String getMessage(Integer code, Object... args) {
        return getMessage(String.valueOf(code), args);
    }

    /**
     * @param code ：对应messages配置的key.
     * @param args : 数组参数.
     * @return String
     */
    public String getMessage(String code, Object... args) {
        return getMessage(code, args, "");
    }

    /**
     * @param code           ：对应messages配置的key.
     * @param args           : 数组参数.
     * @param defaultMessage : 没有设置key的时候的默认值.
     * @return String
     */
    public String getMessage(String code, Object[] args, String defaultMessage) {
        return messageSource.getMessage(code, args, defaultMessage, LocaleContextHolder.getLocale());
    }
}