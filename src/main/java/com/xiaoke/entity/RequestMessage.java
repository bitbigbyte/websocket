package com.xiaoke.entity;

import lombok.Data;
import java.io.Serializable;

/**
 * 前端发给后端的消息格式
 */
@Data
public class RequestMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String toName;
    private String msg;
}
