package ZhiJianHu.Service;



import ZhiJianHu.Common.Message;
import ZhiJianHu.Common.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceGUI extends JFrame implements Service.UserServiceListener {
    private static final Logger log = LoggerFactory.getLogger(ServiceGUI.class);
    private static JTextArea onlineUsersTextArea;
    private JTextArea messageTextArea;
    private JTextField messageInputField;
    private JButton sendMessageButton;
    private JButton kickUserButton;
    private static String name;

    private static List<String> onlineUsers;

    public ServiceGUI() {
        //遍历在线用户！展示！
        onlineUsers = new ArrayList<>();
        setTitle("Server GUI");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        onlineUsersTextArea = new JTextArea();
        onlineUsersTextArea.setEditable(false);
        JScrollPane onlineUsersScrollPane = new JScrollPane(onlineUsersTextArea);

        messageTextArea = new JTextArea();
        messageTextArea.setEditable(false);
        JScrollPane messageScrollPane = new JScrollPane(messageTextArea);

        messageInputField = new JTextField();
        sendMessageButton = new JButton("发送广播");
        kickUserButton = new JButton("下线用户");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageInputField, BorderLayout.CENTER);
        inputPanel.add(sendMessageButton, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(kickUserButton);

        setLayout(new BorderLayout());
        add(onlineUsersScrollPane, BorderLayout.WEST);
        add(messageScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.NORTH);



         Action listener =  (new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageInputField.getText();
                if (!message.isEmpty()) {
                    messageTextArea.append("Server: " + message + "\n");
                    messageInputField.setText("");
                    // 发送消息给所有用户
                    broadcastMessage(message);
                }
            }
        });
         sendMessageButton.addActionListener(listener);
        messageInputField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"listener");
        messageInputField.getActionMap().put("listener",listener);

        kickUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userToKick = JOptionPane.showInputDialog("输入用户名称");
                if (userToKick != null && !userToKick.isEmpty()) {
                    kickUser(userToKick);
                }
            }
        });


        new Thread(()->{
            Service service1 = new Service();
            service1.addUserServiceListener(this);
            addOnlineUser();
        }).start();
    }

    public static  void addOnlineUser() {
        onlineUsers.add(name);
        updateOnlineUsersList();
    }

    public static void removeOnlineUser(String username) {
        //下线
        onlineUsers.remove(username);
        updateOnlineUsersList();
    }

    private static void updateOnlineUsersList() {
        onlineUsersTextArea.setText("");
        for (String user : onlineUsers) {
            onlineUsersTextArea.append(user + "\n");
            log.info(user);
        }
    }

    private void broadcastMessage(String message) {
        // 这里需要实现广播消息给所有用户的功能
        // 可以通过Service类中的方法来实现
        Message mes=new Message();
        mes.setContent(message);
        mes.setSender("服务器");
        mes.setReceiver("allpeople");
        mes.setMessageType(MessageType.MESSAGE_ALL_MES);
        LocalDateTime now=LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        mes.setDate(now.format(formatter));
        ServiceThread.Service_send_Mes(mes);
    }

    private void kickUser(String username) {
        Map<String, ServiceThread> stmp = new ServiceThreads().get();
        if(stmp.containsKey(username)){
            ServiceThread.exit(username);
            stmp.remove(username);
            setexit(username);
        }

        // 这里需要实现强制下线用户的功能
        // 可以通过Service类中的方法来实现

    }


    public static void setadd(String name) {
        ServiceGUI.name = name;
        System.out.println(name+"上线");
        addOnlineUser();
    }
    public static void setexit(String name) {
        ServiceGUI.name = name;
        System.out.println(name+"下线");
        removeOnlineUser(name);
    }


    @Override
    public void onUserConnected(String username) {
        System.out.println(username+"上线");
        addOnlineUser();
    }

    @Override
    public void onUserDisconnected(String username) {
        log.info("{}下线",username);
        removeOnlineUser(username);
    }

        public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ServiceGUI().setVisible(true);
            }
        });
    }


}
