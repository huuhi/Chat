package ZhiJianHu.Service;

import ZhiJianHu.Dao.Message_Dao;
import ZhiJianHu.Dao.UserDao;
import ZhiJianHu.Common.Message;
import ZhiJianHu.Common.MessageType;
import ZhiJianHu.Common.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
    public Socket socket;
    private String name;
    private static boolean exit=false;
    private UserDao ud=new UserDao();
    private Message_Dao md=new Message_Dao();

    private ObjectInputStream ois;
    private Message mes;
    private Leave_Mes leave_mes;


    public ServiceThread(Socket socket,String name) {
        this.socket = socket;
        this.name=name;
        ServiceGUI.setadd(name);
        if (leave_mes == null) {
            leave_mes = new Leave_Mes(name, false);
        }
    }

    public static void setExit(boolean exit) {
        ServiceThread.exit = exit;
    }

    @Override
    public void run() {

        //初始化
        while (!exit){
            sendUserList();

            //senduser();
            try {
                ois=new ObjectInputStream(socket.getInputStream());
                mes = (Message) ois.readObject();
                MessageType messageType = mes.getMessageType();
                switch(messageType) {
                    case MESSAGE_ALL_MES -> SendAll(mes);
                    case MESSAGE_PRIVATE_MES -> PrivateMes() ;
                   // case MESSAGE_SHOW_USER -> sendUserList();
                    case MESSAGE_ADD_USER -> adduser();
                    case MESSAGE_SEND_FILE -> SendFile();
                    case MESSAGE_PRIVATE_FILE -> SendPrivateFile();
                    case MESSAGE_EXIT_MES -> EXIT_MES();
                    case MESSAGE_OPEN_MES -> check();
                    case MESSAGE_USER_DATA ->  sendUserData();
                    case CREATE_GROUP-> createGroup();
                    default -> log.debug("错误消息类型{}",mes.getMessageType());
                }

            }catch (EOFException e){
                log.error("{}下线了",name);
                throw new RuntimeException(e);
            }
            catch (Exception e) {
                log.info("{}下线",name);
            }
        }
        cleanup();
    }
//处理创造群聊
    private void createGroup() {
        //怎么创造群聊？ 怎么让用户加入群聊？
        //先不做，先做私聊功能
        log.info("创建群聊");

    }

    private void sendUserData() {
        Message message = new Message();
        String sender = mes.getSender();
        sender=sender.replace("(在线)","");

        User u = ud.getSingleUser(sender);
        message.setUser(u);
        message.setSender(sender);
        message.setMessageType(MessageType.MESSAGE_USER_DATA);
        log.debug("用户消息{}",u);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            oos.flush();
        } catch (IOException e) {
            log.error("服务端发送用户信息出现错误{}",e);
            throw new RuntimeException(e);
        }

    }

    private void check(){
        //检查消息留言
        log.info("open设置成功");
        leave_mes.setOpen(true); // 传入 true 表示打开私聊窗口
    }


    private void cleanup() {
        ServiceThreads.removeConnection(name);
        sendUserList(); // 更新用户列表

    }


    //强制下线
    public static void exit(String username){
        Message mes=new Message();
        ServiceThread thread = ServiceThreads.getThread(username);
        mes.setMessageType(MessageType.MESSAGE_EXIT_MES);
        mes.setReceiver(username);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(thread.socket.getOutputStream());
            oos.writeObject(mes);
            thread.cleanup();
        } catch (IOException e) {
            log.error("服务端强制退出错误"+e);
        }
    }

    private void EXIT_MES() {
        String sender = mes.getSender();
        log.info("客户{}退出软件",sender);
        ServiceGUI.setexit(sender);
        cleanup();

    }
    public static void Service_send_Mes(Message message){
        //遍历在线用户发送
        Map<String, ServiceThread> sstm = ServiceThreads.get();
        for (ServiceThread st:sstm.values()){
            st.SendAll(message);
            break;
        }
    }


    private void SendPrivateFile() {
        String receiver = mes.getReceiver();
        ServiceThread thread = ServiceThreads.getThread(receiver);
        String sender = mes.getSender();
        User u1 = ud.getSingleUser(sender);
        mes.setUser(u1);
        ServiceThread thread1 = ServiceThreads.getThread(sender);
        try {
            ObjectOutputStream oos1 = new ObjectOutputStream(thread1.socket.getOutputStream());
            oos1.writeObject(mes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(thread != null){
            try{
                ObjectOutputStream oos = new ObjectOutputStream(thread.socket.getOutputStream());
                oos.writeObject(mes);
                oos.flush();
            } catch (IOException e) {
                log.error("私发文件错误{}",e);
                throw new RuntimeException(e);
            }
        }else{
            md.addmes(mes);
        }


    }


    private void SendFile() {
        log.info("进入发送文件方法");
            //循环给所有人发送
        List<User> users = ud.getAllUsers();

        String sender = mes.getSender();
        User u1 = ud.getSingleUser(sender);
        mes.setUser(u1);
        ServiceThread thread1 = ServiceThreads.getThread(sender);
        try {
            ObjectOutputStream oos1 = new ObjectOutputStream(thread1.socket.getOutputStream());
            oos1.writeObject(mes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for(User u:users){
            User u2 = ud.getSingleUser(mes.getSender());
            mes.setUser(u2);
            String name = u.getName();
            mes.setReceiver(u.getName());
            if(!name.equals(mes.getSender())){
                    //得到线程发送
            ServiceThread thread = ServiceThreads.getThread(u.getName());
                if(thread==null){
                    log.debug("添加文件成功");
                    md.addmes(mes);
                    continue;
                }
                try {
                    log.info("正在发送文件");
                    ObjectOutputStream oos = new ObjectOutputStream(thread.socket.getOutputStream());
                    oos.writeObject(mes);
                    oos.flush();
                    log.info("发送文件成功");
                } catch (IOException e) {
                    log.error("服务端发送文件消息错误"+e);
                    //throw new RuntimeException(e);
                }
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

    /*

     */

    private void sendUserList() {
        List<User> allUsers = ud.getAllUsers();
        StringBuilder names = new StringBuilder();

        // 构建用户列表字符串
        for (User u : allUsers) {
            String userName = u.getName();
            if (ServiceThreads.contains(userName)) {
                userName += "(在线)";
            }
            names.append(userName).append(",");
        }
        if(!names.isEmpty()){
            names.deleteCharAt(names.length() - 1);
        }

        Message message = new Message();
        message.setContent(names.toString());
        message.setMessageType(MessageType.MESSAGE_SHOW_USER);

        // 发送用户列表给所有在线用户
        Map<String, ServiceThread> sstm = ServiceThreads.get();
        synchronized (sstm) {
            for (ServiceThread thread : sstm.values()) {
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(thread.socket.getOutputStream());
                    oos.writeObject(message);
                    oos.flush();
                } catch (IOException e) {
                    log.error("发送用户列表时发生错误: {}", e.getMessage());
                }
            }
        }
    }

    private void PrivateMes() {
    // 拿到接收者的线程，发送过去
        String receiver = mes.getReceiver();
        receiver=receiver.replace("(在线)","");
        ServiceThread thread = ServiceThreads.getThread(receiver);
        User u = ud.getSingleUser(mes.getSender());
        mes.setUser(u);
        String sender = mes.getSender();
        User u1 = ud.getSingleUser(sender);
        mes.setUser(u1);
        ServiceThread thread1 = ServiceThreads.getThread(sender);
        try {
            ObjectOutputStream oos1 = new ObjectOutputStream(thread1.socket.getOutputStream());
            oos1.writeObject(mes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (thread == null) {
            md.addmes(mes);
            log.error("未找到接收者 {} 的线程", receiver);
            return;
        }



        try {
            ObjectOutputStream oos = new ObjectOutputStream(thread.socket.getOutputStream());
            oos.writeObject(mes);
            oos.flush();
        } catch (IOException e) {
            log.error("服务端发送私聊时出现错误", e);
            // throw new RuntimeException(e);
        }
}


    private void SendAll(Message mes) {
        //发消息给所有人，返回客户端,
        //遍历数据库！
        List<User> allUsers = ud.getAllUsers();
        //先给发送者发送消息！
        String sender = mes.getSender();
        User u1 = ud.getSingleUser(sender);
        mes.setUser(u1);
        ServiceThread thread1 = ServiceThreads.getThread(sender);
        try {
            ObjectOutputStream oos1 = new ObjectOutputStream(thread1.socket.getOutputStream());
            oos1.writeObject(mes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        synchronized (allUsers){
            for(User user : allUsers){
                User u = ud.getSingleUser(sender);
                mes.setUser(u);
                String name=user.getName();
                mes.setReceiver(name);
                if(!name.equals(mes.getSender())){
                    //得到线程发送
                    ServiceThread thread = ServiceThreads.getThread(name);
                    if(thread==null){
                        md.addmes(mes);
                        //log.error("服务端线程是null"+name);
                        continue;
                    }
                    try {
                        log.debug("消息是{}",mes);
                        ObjectOutputStream oos = new ObjectOutputStream(thread.socket.getOutputStream());
                        oos.writeObject(mes);
                        oos.flush();
                    } catch (IOException e) {
                        log.error("服务端发送群聊消息错误"+e);
                        //throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
