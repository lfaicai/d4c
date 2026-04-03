package org.faicai.d4c.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author faicai.lan
 */
@Getter
@Setter
public class BaseException extends Exception {
    private final Integer code;
    private String userMsg;
    private final Object[] args;

    public BaseException(Integer code, Object... args) {
        this(code, "", args);
    }

    public BaseException(Integer code, String userMsg, Object... args) {
        this(code, userMsg, "", args);
    }

    public BaseException(Integer code, Throwable cause, Object... args) {
        this(code, null, cause, args);
    }

    public BaseException(Integer code, String userMsg, String developerMsg, Object... args) {
        super(StringUtils.hasText(developerMsg) ? developerMsg :  userMsg);
        this.code = code;
        this.userMsg = userMsg;
        this.args = args;
    }

    public BaseException(Integer code, String userMsg, Throwable cause, Object... args) {
        super(cause);
        this.code = code;
        this.userMsg = userMsg;
        this.args = args;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (!StringUtils.hasText(message) || (message.startsWith(BaseExceptionHandler.LEFT_BRACE) && message.endsWith(BaseExceptionHandler.RIGHT_BRACE))) {
            return userMsg;
        }
        return message;
    }

    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        if (sw.toString().length() < BaseExceptionHandler.EXCEPTION_MSG_LENGTH) {
            return sw.toString();
        }
        return sw.toString().substring(0, BaseExceptionHandler.EXCEPTION_MSG_LENGTH);
    }

}