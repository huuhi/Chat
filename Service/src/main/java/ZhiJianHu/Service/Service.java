package ZhiJianHu.Service;

import ZhiJianHu.Common.Message;
import ZhiJianHu.Common.MessageType;
import ZhiJianHu.Common.User;
import ZhiJianHu.Common.Dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
                User users = (User)ois.readObject();
            String name = users.getName();
            Message ms=new Message();
                User user = ud.login(users.getName(), users.getPwd());
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
        }catch (Exception e) {
                log.error("服务器出现错误"+e);
                throw new RuntimeException(e);
            }
        }




    public static void main(String[] args) {

    }

}
