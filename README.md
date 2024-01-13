# websocket
## 介绍
* 本项目为基于***spring-boot***开发的***websocket***聊天室
* 端口号是8080
## 运行
* 前端可以使用 [websocket-ui](http://wstool.js.org/)
* 后端接口路径为 ***ws://localhost:8080/chat/{userName}***
* 如果使用内网穿透，直接把 ***localhost:8080*** 替换为穿透后的 ***http://*** 后面的部分
* ***userName***为用户名，可以自定义
## 注意
* https加密网页内的请求只能使用加密请求，即 ***wss***
* http请求的网页不仅可以使用不加密的http请求，还可以使用加密的https请求，即 ***ws*** 和 ***wss***
## 消息格式
### 发送消息格式
> toName为发送给的用户名，toName为all表示发送给所有人，msg为发送的消息
```JSON
{
  "toName":"all",
  "msg":"hello"     
}
```
### 返回消息格式
> data为发送的消息，fromName为发送者，isSystemMessage为是否为系统消息
```JSON
{
  "data":"hello",
  "fromName":"张三",
  "isSystemMessage":false
}
```
