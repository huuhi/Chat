package ZhiJianHu.ClientGui;

import ZhiJianHu.Common.Message;
import ZhiJianHu.Common.MessageType;
import ZhiJianHu.Common.User;
import ZhiJianHu.Dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

public class ChatRoomUI extends JFrame implements KeyListener {

    private static final Logger log = LoggerFactory.getLogger(ChatRoomUI.class);
    private JTextPane messageArea; // 消息显示区
    private JTextField messageField; // 消息输入框
    private JButton sendButton; // 发送按钮
    JList<String> userList; // 用户列表
    DefaultListModel<String> userListModel; // 用户列表模型
    private Socket socket;
    private Thread messageReceiverThread;
    private String username;
    private Message message;
    private UserDao ud = new UserDao();

    public ChatRoomUI(String username, Socket socket) {
        this.username = username;
        this.socket = socket;
        setTitle("群聊 - " + username);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 创建消息显示区
        messageArea = new JTextPane();
        messageArea.setEditable(false);
        messageArea.setContentType("text/html");
        messageArea.setBackground(Color.decode("#FAFAFA"));
        messageArea.setForeground(Color.decode("#4A4A4A"));
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // 创建消息输入框
        messageField = new JTextField();
        messageField.setFont(new Font("KaiTi", Font.PLAIN, 14));
        messageField.setBorder(BorderFactory.createLineBorder(Color.decode("#CCCCCC"), 2));
        messageField.addKeyListener(this);

        // 创建发送按钮
        sendButton = createButton("发送", Color.decode("#007AFF"), event -> sendMessage(username));

        // 创建用户列表
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setFont(new Font("KaiTi", Font.PLAIN, 14));
        userList.setBackground(Color.decode("#FAFAFA"));
        userList.setForeground(Color.decode("#4A4A4A"));
        userList.addMouseListener(new UserListMouseListener(this, username));

        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // 创建消息输入面板
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBackground(Color.decode("#FFFFFF"));
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // 将组件添加到窗口
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(userScrollPane, BorderLayout.EAST); // 将用户列表添加到右侧

        // 设置窗口背景颜色
        getContentPane().setBackground(Color.decode("#F5F5F5"));

        startMessageReceiverThread();
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
    public void sendMessage(String username) {
        String message = messageField.getText();
        if (message.isEmpty()) {
            log.warn("消息输入框为空，无法发送消息");
            return;
        }
        Message mes = new Message();
        mes.setSender(username);
        mes.setMessageType(MessageType.MESSAGE_ALL_MES);
        mes.setContent(message);
        LocalDateTime now = LocalDateTime.now();
        mes.setDate(now);
        // 发送给服务端返回
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(mes);
            oos.flush();
        } catch (IOException e) {
            log.info("客户端发送消息出现问题" + e);
            throw new RuntimeException(e);
        }
        // 这里需要读服务端发送的消息
        update(mes);
        messageField.setText("");
    }

    public void update(Message mes) {
    if (mes != null) {
        User user = ud.getUser(mes.getSender());
        String avatarPath = user.getImage(); // 获取头像路径
        String content = mes.getContent();
        String date = mes.getDate().toString();
        String sender = mes.getSender();

        // 构建HTML内容
        StringBuilder htmlContent = new StringBuilder();
        if (avatarPath != null && !avatarPath.isEmpty()) {
            File imageFile = new File(avatarPath);
            if (imageFile.exists()) {
                // 使用 file: 协议
                htmlContent.append("<img src='file:").append(imageFile.getAbsolutePath().replace("\\", "/")).append("' width='30' height='30'> ");
            } else {
                htmlContent.append("[图片不存在] ");
                log.warn("图片文件不存在: " + avatarPath);
            }
        } else {
            htmlContent.append("[无头像] ");
        }
        htmlContent.append("<b>").append(sender).append("</b>: ").append(content).append("<br>");
        htmlContent.append("<span style='font-size:10px;'>").append(date).append("</span><br>");

        // 调试输出
        log.debug("插入消息: " + htmlContent.toString());

        // 插入到消息区域
        try {
            HTMLEditorKit kit = (HTMLEditorKit) messageArea.getEditorKit();
            HTMLDocument doc = (HTMLDocument) messageArea.getDocument();
            int docLength = doc.getLength(); // 获取文档的实际长度
            log.debug("当前文档长度: " + docLength);
            kit.insertHTML(doc, docLength, htmlContent.toString(), 0, 0, null);
            messageArea.setCaretPosition(doc.getLength());
        } catch (BadLocationException | IOException e) {
            log.error("插入消息时出现问题: " + e.getMessage(), e);
        }
    } else {
        log.error("message是null！");
    }
}







    private void startMessageReceiverThread() {
        messageReceiverThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    try {
                        message = (Message) ois.readObject();
                        MessageType messageType = message.getMessageType();
                        switch (messageType) {
                            case MESSAGE_ALL_MES -> SwingUtilities.invokeLater(() -> update(message));
                            case MESSAGE_SHOW_USER -> get();
                            case MESSAGE_ADD_USER -> adduser();
                        }
                    } catch (EOFException e) {
                        log.error("连接已关闭: " + e.getMessage(), e);
                        break; // 连接已关闭，退出循环
                    } catch (StreamCorruptedException e) {
                        log.error("数据损坏: " + e.getMessage(), e);
                        break; // 数据损坏，退出循环
                    } catch (IOException | ClassNotFoundException e) {
                        log.error("接收消息时出现问题: " + e.getMessage(), e);
                        break; // 其他IO异常，退出循环
                    }
                }
            } catch (IOException e) {
                log.error("初始化 ObjectInputStream 时出现问题: " + e.getMessage(), e);
            } finally {
                // 确保资源释放
                if (socket != null && !socket.isClosed()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        log.error("关闭 socket 时出现问题: " + e.getMessage(), e);
                    }
                }
            }
        });
        messageReceiverThread.start();
    }

    private void adduser() {
        String content = message.getContent();
        addUser(content);
    }

    // 获取用户信息的线程
    public void get() {
        // 这里只是添加在左边，
        System.out.println(message.getContent());
        String[] split = message.getContent().split(",");
        for (String user : split) {
            addUser(user);
        }
    }

    // 添加用户的方法
    private void addUser(String name) {
        userListModel.addElement(name);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_ENTER) {
            sendMessage(username);
            System.out.println("回车了");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}

// 用户列表鼠标监听器类
class UserListMouseListener extends MouseAdapter {

    private final ChatRoomUI chatRoomUI;
    private final String username;

    public UserListMouseListener(ChatRoomUI chatRoomUI, String username) {
        this.chatRoomUI = chatRoomUI;
        this.username = username;
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        if (evt.getClickCount() == 2) { // 双击事件
            int index = chatRoomUI.userList.locationToIndex(evt.getPoint());
            String selectedUser = chatRoomUI.userListModel.get(index);
            if (!selectedUser.equals(username)) {
                showUserOptions(selectedUser);
            }
        }
    }

    private void showUserOptions(String selectedUser) {
        Object[] options = {"查看个人信息", "私聊"};
        int choice = JOptionPane.showOptionDialog(
                chatRoomUI,
                "请选择操作",
                "选项",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            new UserInfoUI(selectedUser).setVisible(true);
        } else if (choice == 1) {
            new PrivateChatUI(username, selectedUser).setVisible(true);
        }
    }
}
