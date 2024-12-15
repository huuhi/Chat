package ZhiJianHu.Service;

import ZhiJianHu.Common.Dao.UserDao;
import ZhiJianHu.Common.Message;
import ZhiJianHu.Common.MessageType;
import ZhiJianHu.Common.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/15
 * 说明:
 */
public class ServiceThread extends Thread{
    private static final Logger log= LoggerFactory.getLogger(ServiceThread.class);
    private Socket socket;
    private String name;
    private boolean exit=false;
    private UserDao ud=new UserDao();

    private ObjectInputStream ois;
    private Message mes;


    public ServiceThread(Socket socket,String name) {
        this.socket = socket;
        this.name=name;
    }

    @Override
    public void run() {
        //初始化
        while (!exit){
            try {
                ois=new ObjectInputStream(socket.getInputStream());
                mes = (Message) ois.readObject();
                MessageType messageType = mes.getMessageType();
                switch(messageType) {
                    case MESSAGE_ALL_MES -> SendAll();
                    case MESSAGE_PRIVATE_MES -> PrivateMes();
                    case MESSAGE_SHOW_USER -> senduser();
                    case MESSAGE_ADD_USER -> adduser();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }



    }

    private void adduser() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(mes);
            oos.flush();
        } catch (IOException e) {
            log.error("添加用户时发生错误"+e);
            throw new RuntimeException(e);
        }

    }

    private void senduser() {
        //发送用户名字，直接获得所有用户
        List<User> allUsers = ud.getAllUsers();
        String names="";
        for (User u:allUsers){
            names=names+u.getName()+",";
        }
        Message message = new Message();
        message.setContent(names);
        message.setMessageType(MessageType.MESSAGE_SHOW_USER);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            oos.flush();
        } catch (IOException e) {
            log.error("获取用户时发生错误"+e);
            throw new RuntimeException(e);
        }


    }

    private void PrivateMes() {
    }

    private void SendAll() {
        //发消息给所有人，返回客户端
        //遍历集合！
        Map<String, ServiceThread> sst = ServiceThreads.get();
        Iterator<String> iterator = sst.keySet().iterator();
        while (iterator.hasNext()){
            String name=iterator.next();
            if(!name.equals(mes.getSender())){
                //得到线程发送
                ServiceThread thread = ServiceThreads.getThread(name);
                if(thread==null){
                    log.error("服务端线程是null"+name);
                    return;
                }
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(thread.socket.getOutputStream());
                    oos.writeObject(mes);
                    oos.flush();
                } catch (IOException e) {
                    log.error("服务端发送群聊消息错误"+e);
                    throw new RuntimeException(e);
                }
            }
        }




    }
}
