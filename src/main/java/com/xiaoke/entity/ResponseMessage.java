package com.xiaoke.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

/**
 * 后端发给前端的消息格式
 */
@Data
@AllArgsConstructor
public class ResponseMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean isSystemMessage;
    private String fromName;
    private Object msg;
}
