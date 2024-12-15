package ZhiJianHu.Client;

import ZhiJianHu.ClientGui.ChatRoomUI;
import ZhiJianHu.Common.Message;
import ZhiJianHu.Common.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
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
    private String name;
    private ObjectInputStream ois;
    private Message mes;
    private boolean exit=false;
    public ClientConnectServiceThread(Socket s){
        this.socket=s;
    }

    @Override
    public void run() {
        //怎么说？写方法读消息，然后在GUI页面展示！
        while (!exit) {
            try {


            } catch (Exception e) {
                log.error("客户端线程问题"+e);
                throw new RuntimeException(e);
            }

            //一直等待接收服务端的消息

        }


    }

}
