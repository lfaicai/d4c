package org.faicai.d4c.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.faicai.d4c.constant.CommonConstants;
import org.faicai.d4c.constant.ResponseCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @param <T>
 * @author lanfaicai
 */
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class R<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private int code;

    @Getter
    @Setter
    private String msg;

    @Getter
    @Setter
    private String timestamp = String.valueOf(System.currentTimeMillis());

    @Getter
    @Setter
    private T data;

    /**
     * code非200时返回错误信息，开发者排查问题;需要base64解码
     */
    @Getter
    @Setter
    private String developerMsg;

    public static <T> R<T> ok() {
        return restResult(null, ResponseCode.OK, null, null);
    }
    public static <T> R<T> rBoolean(boolean b) {
        return restResult(null, b ? ResponseCode.OK : ResponseCode.SYSTEM_EXCEPTION, null, null);
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, ResponseCode.OK, null, null);
    }

    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, ResponseCode.OK, msg, null);
    }

    public static <T> R<T> failed() {
        return restResult(null,  ResponseCode.SYSTEM_EXCEPTION, null, null);
    }

    public static <T> R<T> ok(Integer code) {
        return restResult(null, code, null, null);
    }

    public static <T> R<T> failed(String msg) {
        return restResult(null,  ResponseCode.SYSTEM_EXCEPTION, msg, null);
    }

    public static <T> R<T> failed(T data) {
        return restResult(data,  ResponseCode.SYSTEM_EXCEPTION, null, null);
    }

    public static <T> R<T> failed(T data, String msg, String developerMsg) {
        return restResult(data, ResponseCode.SYSTEM_EXCEPTION, msg, developerMsg);
    }

    public static <T> R<T> failed(int code, String msg) {
        return restResult(null, code, msg, null);
    }

    public static <T> R<T> failed(Integer code) {
        return restResult(null, code, null, null);
    }

    private static <T> R<T> restResult(T data, int code, String msg, String developerMsg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        apiResult.setDeveloperMsg(developerMsg);
        return apiResult;
    }


    @Override
    public String toString() {
        return "{\n" +
                "    \"code\": " + code + ",\n" +
                "    \"data\": " + data + ",\n" +
                "    \"msg\": \"" + msg + "\",\n" +
                "    \"developerMsg\": \"" + developerMsg + "\",\n" +
                "    \"timestamp\": \"" + timestamp + "\"\n" +
                "}";

    }
}
