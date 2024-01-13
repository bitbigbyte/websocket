package com.xiaoke.entity;

import lombok.Data;
import java.io.Serializable;

/**
 * 通用返回结果，服务端响应的数据最终都会封装成此对象
 * @param <T>
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    //使用boolean时，生成的get方法会把is省略，而且FastJson使用的是getter方法生成json，会导致is消失，所以使用包装类Boolean刻意避免此问题
    private Boolean isSystemMessage;
    private String fromName;
    private T data;

    public static <T> R<T> createMsg(Boolean isSystemMessage,String fromName,T object) {
        R<T> r = new R<>();
        r.isSystemMessage = isSystemMessage;
        r.fromName = fromName;
        r.data = object;
        return r;
    }
}
