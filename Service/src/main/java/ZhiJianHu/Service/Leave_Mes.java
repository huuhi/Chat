package ZhiJianHu.Service;

import ZhiJianHu.Common.Dao.Message_Dao;
import ZhiJianHu.Common.Message;
import ZhiJianHu.Common.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/17
 * 说明:
 */
public class Leave_Mes extends Thread{
     private static final Logger log= LoggerFactory.getLogger(Leave_Mes.class);
    private String name;
    private Message_Dao md=new Message_Dao();
    private ArrayList<Message> messages;
    private ServiceThread thread;
    private boolean open;



    public Leave_Mes(boolean open){
        this.open=open;
    }

    public Leave_Mes(String name,boolean open){
        this.open=open;
        this.name=name;
        start();
    }

    @Override
    public void run() {
        while(true){
            messages = (ArrayList<Message>) md.getmessage(name);
            if(messages.isEmpty()){
                log.info("没有信息");
                return;
            }
            for(Message mes:messages){
                MessageType messageType = mes.getMessageType();
                if(messageType.equals(MessageType.MESSAGE_ALL_MES)||messageType.equals(MessageType.MESSAGE_SEND_FILE)){
                     send(mes);
                }else if(open&&messageType.equals(MessageType.MESSAGE_PRIVATE_MES)){
                    privateMes(mes);
                }else if(open&&messageType.equals(MessageType.MESSAGE_PRIVATE_FILE)){
                    privateMes(mes);
                }
                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    log.error("睡觉出现异常！");
                    System.exit(1);
                }

            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            messages.clear();
        }
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    private void privateMes(Message mes) {
        //私聊怎么处理？？？
        thread=ServiceThreads.getThread(name);
        if(thread!=null){
            try {
                md.removemes(mes.getId());
                ObjectOutputStream oos = new ObjectOutputStream(thread.socket.getOutputStream());
                oos.writeObject(mes);
                oos.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            log.debug("{}是null",name);
        }

    }

    private synchronized void send(Message mes) {
        //发送消息，获得接收者的线程
        //发送
        thread = ServiceThreads.getThread(name);

        if(thread!=null){
            try {
                md.removemes(mes.getId());
               ObjectOutputStream oos=new ObjectOutputStream(thread.socket.getOutputStream());
                log.debug("消息是这样的{}",mes);
                oos.writeObject(mes);
                oos.flush();
            } catch (IOException e) {
                log.error("发送留言重新错误"+e);
                System.exit(1);
            }

        }else{
            log.error("{}是null",thread);
        }

    }


}
