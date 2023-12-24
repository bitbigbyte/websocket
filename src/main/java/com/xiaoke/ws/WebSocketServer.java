package com.xiaoke.ws;

import com.alibaba.fastjson.JSON;
import com.xiaoke.entity.RequestMessage;
import com.xiaoke.entity.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于用户之间相互发送消息
 */
@Slf4j
@ServerEndpoint("/chat/{userName}")
@Component
public class WebSocketServer {

    //用来存储所有的的WebSocketServer对象
    private static final Map<String, WebSocketServer> onlineUsers = new ConcurrentHashMap<>();
    private static long COUNT = 0;
    private Session session;
    private String userName;

    /**
     * 连接建立时被调用
     *
     * @param session  websocket的session
     * @param userName 用户昵称
     * @Description @PathParam注解用于获取路径参数,是websocket自己实现的一个注解
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userName") String userName) throws IOException {
        this.session = session;
        this.userName = userName;
        if (onlineUsers.containsKey(userName)) {
            onlineUsers.remove(userName);
            onlineUsers.put(userName, this);
        } else {
            onlineUsers.put(userName, this);
            addCount();
        }
        //广播消息,通知所有用户
        String msg = String.valueOf(new ResponseMessage(true, null, onlineUsers.keySet()));
        broadcast(msg);
        log.info("当前在线人数为：{}", getCount());
    }

    /**
     * 接收到客户端发送的数据时被调用
     * @param message 用户发送的消息
     */
    @OnMessage
    public void onMessage(String message) throws IOException {
        RequestMessage requestMessage = JSON.parseObject(message, RequestMessage.class);
        if (requestMessage.getMsg().isEmpty()) {
            this.session.getBasicRemote().sendText("消息不能为空");
        } else if (requestMessage.getToName().equals("all")) {
            String msg = String.valueOf(new ResponseMessage(false, this.userName, requestMessage.getMsg()));
            broadcast(msg);
        } else if (onlineUsers.containsKey(requestMessage.getToName())) {
            //获取目的用户的Session对象
            WebSocketServer webSocketServer = onlineUsers.get(requestMessage.getToName());
            Session session = webSocketServer.session;
            //发送消息
            String msg = String.valueOf(new ResponseMessage(false, this.userName, requestMessage.getMsg()));
            session.getBasicRemote().sendText(msg);
        } else {
            this.session.getBasicRemote().sendText("没有指定的目的联系人");
        }
    }

    /**
     * 连接关闭时被调用
     */
    @OnClose
    public void onClose() throws IOException {
        //从onlineUsers中删除当前用户WebSocketServer对象
        if (onlineUsers.containsKey(this.userName)) {
            onlineUsers.remove(userName);
            subCount();
            //通知其他用户，该用户下线了
            String msg = String.valueOf(new ResponseMessage(true, null, onlineUsers.keySet()));
            broadcast(msg);
            log.info("当前在线人数为：{}", getCount());
        }
    }

    /**
     * 连接错误时被调用
     * @param error 异常
     */
    @OnError
    public void onError(Throwable error) throws IOException {
        String msg = String.valueOf(new ResponseMessage(true, null, "发生错误："+error.getMessage()));
        this.session.getBasicRemote().sendText(msg);
        log.error("用户{}发生错误，原因是：{}",this.userName,error.getMessage());
    }

    /**
     * 广播方法
     * @param message 消息
     */
    private void broadcast(String message) throws IOException {
        //遍历Map集合
        for(WebSocketServer webSocketServer:onlineUsers.values()){
            Session session=webSocketServer.session;
            //发送消息
            session.getBasicRemote().sendText(message);
        }
    }

    public static synchronized void addCount(){
        WebSocketServer.COUNT++;
    }

    public static synchronized void subCount(){
        WebSocketServer.COUNT--;
    }

    public static synchronized long getCount(){
        return WebSocketServer.COUNT;
    }
}