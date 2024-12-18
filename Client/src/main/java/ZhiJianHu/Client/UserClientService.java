package ZhiJianHu.Client;

import ZhiJianHu.ClientGui.ChatRoomUI;
import ZhiJianHu.Common.Message;
import ZhiJianHu.Common.MessageType;
import ZhiJianHu.Common.User;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/15
 * 说明:验证并且开启线程存储线程
 */
public class UserClientService {
    private static final Logger log= LoggerFactory.getLogger(UserClientService.class);
    private static User u=new User();
    @Getter
    private static Socket socket;
    private static final int PORT=9898;
    private static LocalDateTime now = LocalDateTime.now();
    private  MessageType mt = null;
    private  Message mes;
    public UserClientService(Message message,MessageType mt){
        //根据消息类型发送消息
        this.mes=message;
        this.mt=mt;
    }

    public UserClientService() {

    }

    public static boolean isLogin(String name, String password) {
        boolean flag = false;
        u.setName(name);
        u.setPwd(password);
        try {
            socket=new Socket(InetAddress.getLocalHost(),PORT);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u);
            Message mes = (Message)ois.readObject();
            if(mes.getMessageType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)){
                 ChatRoomUI chatRoomUI = new ChatRoomUI(name, socket);
                chatRoomUI.setVisible(true);
                ClientConnectServiceThread cl = new ClientConnectServiceThread(socket,chatRoomUI);
                cl.start();
                ClientThreads.addClientConnectServiceThread(name,cl);
                flag = true;
            }

        } catch (Exception e) {
            log.error("客户端验证用户出现问题"+e);
            System.exit(1);
        }
        return flag;

    }
    //
    public static void getUser(){
        Message message = new Message();
        message.setMessageType(MessageType.MESSAGE_SHOW_USER);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
