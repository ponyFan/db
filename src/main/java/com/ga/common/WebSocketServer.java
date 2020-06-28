package com.ga.common;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author zelei.fan
 * @date 2019/12/11 15:27
 * @description websocket 客户端通过以下方式监听，实时推送给页面
 * var url = "ws://192.168.9.24:8080/websocket";
 *     var socket = null;
 *     if ('WebSocket' in window){
 *         socket = new WebSocket(url);
 *     }else {
 *         window.alert("浏览器不支持WebSocket")
 *     }
 *
 *     socket.onopen = function () {
 *         console.log("socket open")
 *     }
 *
 *     socket.onmessage = function (msg) {
 *         console.log("get msg" + msg.data)
 *     }
 *
 *     window.onbeforeunload = function () {
 *         socket.close()
 *     }
 */
@ServerEndpoint("/websocket")
@Component
public class WebSocketServer {

    private Session session;

    private static int online = 0;

    private static CopyOnWriteArrayList<WebSocketServer> sockets = new CopyOnWriteArrayList<>();

    @OnOpen
    public void onOpen(Session session){
        sockets.add(this);
        this.session = session;
        System.out.println("socket client connect");
    }

    @OnClose
    public void onClose(){
        sockets.remove(this);
        System.out.println("socket client close");
    }

    public void send(String msg){
        try {
            if (null != session){
                this.session.getBasicRemote().sendText(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMsg(String msg){
        for (WebSocketServer socket : sockets) {
            socket.send(msg);
        }
    }
}
