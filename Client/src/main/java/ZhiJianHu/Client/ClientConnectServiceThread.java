package ZhiJianHu.Client;

import ZhiJianHu.ClientGui.ChatRoomUI;
import ZhiJianHu.ClientGui.PrivateChatUI;
import ZhiJianHu.ClientGui.UserInfoUI;
import ZhiJianHu.Common.Message;
import ZhiJianHu.Common.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/15
 * 说明:
 */
public class ClientConnectServiceThread extends Thread{
    private static final Logger log = LoggerFactory.getLogger(ClientConnectServiceThread.class);
    private Socket socket;
    private Message mes;
    private static boolean exit=false;
    private ChatRoomUI chatRoomUI;
    public ClientConnectServiceThread(Socket s,ChatRoomUI chatRoomUI){
        this.socket=s;
        this.chatRoomUI=chatRoomUI;
    }

    @Override
    public void run() {
        //怎么说？写方法读消息，然后在GUI页面展示！
        while (!exit) {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    try {
                        //还有发文件的方法！
                        mes = (Message) ois.readObject();
                        MessageType messageType = mes.getMessageType();
                        switch (messageType) {
                            case MESSAGE_ALL_MES -> SwingUtilities.invokeLater(() -> ChatRoomUI.update(mes));
                            case MESSAGE_SHOW_USER -> ChatRoomUI.get(mes);
                            case MESSAGE_PRIVATE_MES -> PrivateChatUI.updateMes(mes);
                            case MESSAGE_SEND_FILE -> ChatRoomUI.update(mes);
                            case MESSAGE_PRIVATE_FILE -> PrivateChatUI.updateMes(mes);
                            case MESSAGE_EXIT_MES -> exit();
                            case MESSAGE_USER_DATA -> new UserInfoUI(mes.getSender(),mes.getUser()).setVisible(true);
                        }
                    } catch (EOFException e) {
                        log.error("连接已关闭: " + e.getMessage(), e);
                        break; // 连接已关闭，退出循环
                    } catch (StreamCorruptedException e) {
                        log.error("数据损坏: " + e.getMessage(), e);
                        break; // 数据损坏，退出循环
                    } catch (IOException | ClassNotFoundException e) {
                        log.error("接收消息时出现问题: " + e.getMessage(), e);
                        break; // 其他IO异常，退出循环
                    }
                }
            } catch (IOException e) {
                log.error("初始化 ObjectInputStream 时出现问题: " + e.getMessage(), e);
                System.exit(1);
            } finally {
                // 确保资源释放
                if (socket != null && !socket.isClosed()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        log.error("关闭 socket 时出现问题: " + e.getMessage(), e);
                    }
                }
            }
        }

            //一直等待接收服务端的消息

        }

        private void exit(){
            exit=true;
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            SwingUtilities.invokeLater(() -> {
                chatRoomUI.dispose();
            });
        }


    }


