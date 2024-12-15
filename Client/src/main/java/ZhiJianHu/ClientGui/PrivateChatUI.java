package ZhiJianHu.ClientGui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PrivateChatUI extends JFrame {

    private JTextArea messageArea; // 消息显示区
    private JTextField messageField; // 消息输入框
    private JButton sendButton; // 发送按钮
    private String myNickname; // 我的昵称
    private String otherNickname; // 对方的昵称

    public PrivateChatUI(String myNickname, String otherNickname) {
        this.myNickname = myNickname;
        this.otherNickname = otherNickname;
        setTitle("私聊 - " + myNickname + " 和 " + otherNickname);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 创建消息显示区
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setFont(new Font("KaiTi", Font.PLAIN, 14));
        messageArea.setBackground(Color.decode("#FAFAFA"));
        messageArea.setForeground(Color.decode("#4A4A4A"));
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // 创建消息输入框
        messageField = new JTextField();
        messageField.setFont(new Font("KaiTi", Font.PLAIN, 14));
        messageField.setBorder(BorderFactory.createLineBorder(Color.decode("#CCCCCC"), 2));

        // 创建发送按钮
        sendButton = createButton("发送", Color.decode("#007AFF"), event -> sendMessage());

        // 创建消息输入面板
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBackground(Color.decode("#FFFFFF"));
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // 将组件添加到窗口
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        // 设置窗口背景颜色
        getContentPane().setBackground(Color.decode("#F5F5F5"));
    }

    // 创建按钮的方法
    private JButton createButton(String text, Color background, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("KaiTi", Font.BOLD, 14));
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.addActionListener(listener);
        return button;
    }

    // 发送消息的方法
    private void sendMessage() {
        String message = messageField.getText();
        if (!message.trim().isEmpty()) {
            messageArea.append(myNickname + ": " + message + "\n");
            messageField.setText("");
        }
    }
}