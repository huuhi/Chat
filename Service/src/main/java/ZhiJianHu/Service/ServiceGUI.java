package ZhiJianHu.Service;




import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ServiceGUI extends JFrame {
    private JTextArea onlineUsersTextArea;
    private JTextArea messageTextArea;
    private JTextField messageInputField;
    private JButton sendMessageButton;
    private JButton kickUserButton;

    private List<String> onlineUsers;

    public ServiceGUI() {
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

        sendMessageButton.addActionListener(new ActionListener() {
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
           new Service();
        }).start();
    }

    public void addOnlineUser(String username) {
        onlineUsers.add(username);
        updateOnlineUsersList();
    }

    public void removeOnlineUser(String username) {
        onlineUsers.remove(username);
        updateOnlineUsersList();
    }

    private void updateOnlineUsersList() {
        onlineUsersTextArea.setText("");
        for (String user : onlineUsers) {
            onlineUsersTextArea.append(user + "\n");
        }
    }

    private void broadcastMessage(String message) {
        // 这里需要实现广播消息给所有用户的功能
        // 可以通过Service类中的方法来实现
    }

    private void kickUser(String username) {
        // 这里需要实现强制下线用户的功能
        // 可以通过Service类中的方法来实现
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
