package org.faicai.d4c.exception;

import lombok.Getter;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;
    private final Object[] args;
    private String userMsg;

    public BusinessException(Integer code, Object... args) {
        super("");
        this.code = code;
        this.args = args;
    }

    public BusinessException(Integer code) {
        this(code, "");
    }

    public BusinessException(Throwable cause) {
        this(30008, cause);
    }

    public BusinessException(String userMsg) {
        this.code = 30008;
        this.userMsg = userMsg;
        this.args = null;
    }

    public BusinessException(Integer code, String userMsg, String developerMsg, Object... args) {
        super(StringUtils.hasText(developerMsg) ? developerMsg : userMsg);
        this.code = code;
        this.userMsg = userMsg;
        this.args = args;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (!StringUtils.hasText(message)
                || (message.startsWith(BaseExceptionHandler.LEFT_BRACE) && message.endsWith(BaseExceptionHandler.RIGHT_BRACE))
                || !StringUtils.hasText(userMsg)
        ) {
            return MessageFormat.format(StringUtils.hasText(message) ? userMsg : message, args);
        }
        return message;
    }


    public static String getStackTrace(Throwable t) {
        return BaseException.getStackTrace(t);
    }


}