package ZhiJianHu.Service;

import ZhiJianHu.Common.Message;
import ZhiJianHu.Common.MessageType;
import ZhiJianHu.Common.User;
import ZhiJianHu.Common.Dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/15
 * 说明:服务器
 */
public class Service{
    private static final int PORT=9898;
    private ServerSocket ss;
    private Socket socket;
    private static final Logger log= LoggerFactory.getLogger(Service.class);
    UserDao ud=new UserDao();
    public Service(){
        //验证登录！
       System.out.println("服务器在"+PORT+"监听");
       try {
        ss=new ServerSocket(PORT);
        while (true) {
                socket=ss.accept();
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message mes = (Message)ois.readObject();
                if(mes.getMessageType().equals(MessageType.MESSAGE_LOGIN_MES)){
                     login(mes, oos);
                }else if(mes.getMessageType().equals(MessageType.MESSAGE_REGISTER)){
                    register(mes,oos);
                }



        }
        }catch (Exception e) {
                log.error("服务器出现错误"+e);
                System.exit(1);
            }
        }

        private void register(Message mes,ObjectOutputStream oos) {
            String name = mes.getSender();
            int islive = ud.getUserByUsername(name);
            Message message = new Message();
            if(islive>0){
                message.setMessageType(MessageType.MESSAGE_REGISTER_FILE);
            }else{
                message.setMessageType(MessageType.MESSAGE_REGISTER_SUCCEED);
            }
            try {
                oos.writeObject(message);
                oos.flush();
            } catch (IOException e) {
                log.error("注册发生错误{}",e);
            }


        }

    private void login(Message mes, ObjectOutputStream oos) throws IOException {
        String name = mes.getSender();
        String pwd = mes.getContent();
        Message ms=new Message();
        User user = ud.login(name, pwd);
        if(user!=null){
            //登录成功，写成功的消息给客户端
            ms.setMessageType(MessageType.MESSAGE_LOGIN_SUCCEED);
            ServiceThread st = new ServiceThread(socket,name);
            st.start();
            ServiceThreads.addServiceThread(name,st);
            log.info("用户"+name+"登陆成功");
        }else{
            ms.setMessageType(MessageType.MESSAGE_LOGIN_FAILED);
        }
        oos.writeObject(ms);
    }

    private List<UserServiceListener> listeners = new ArrayList<>();

        public void addUserServiceListener(UserServiceListener listener) {
            listeners.add(listener);
        }

        public void removeUserServiceListener(UserServiceListener listener) {
            listeners.remove(listener);
        }

        // 当用户上线时调用
        public void onUserConnected(String username) {
            // 处理用户上线逻辑...
            for (UserServiceListener listener : listeners) {
                listener.onUserConnected(username);
            }
        }

        // 当用户下线时调用
        public void onUserDisconnected(String username) {
            // 处理用户下线逻辑...
            for (UserServiceListener listener : listeners) {
                listener.onUserDisconnected(username);
            }
        }


        public interface UserServiceListener{
            void onUserConnected(String username);
            void onUserDisconnected(String username);
        }

}
