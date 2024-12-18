package ZhiJianHu.ClientGui;


import ZhiJianHu.Common.Message;
import ZhiJianHu.Common.MessageType;
import ZhiJianHu.Common.User;
import ZhiJianHu.Dao.Utils;
import com.alibaba.druid.support.logging.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


public class ChatRoomUI extends JFrame implements KeyListener {

    private static final Logger log = LoggerFactory.getLogger(ChatRoomUI.class);
    private static JTextPane messageArea; // 消息显示区
    private JTextField messageField; // 消息输入框
    private JButton sendButton; // 发送按钮
    JList<String> userList; // 用户列表
    static DefaultListModel<String> userListModel; // 用户列表模型
    private Socket socket;
    private static String username;
    private static ChatRoomUI instance;
    private Map<String, PrivateChatUI> chatWindows = new HashMap<>();

    public void openPrivateChat(String myNickname, String otherNickname, Socket socket) {
        if (chatWindows.containsKey(otherNickname)) {
            // 如果已经存在私聊窗口，直接显示
            PrivateChatUI existingWindow = chatWindows.get(otherNickname);
            existingWindow.toFront();
            existingWindow.requestFocus();
        } else {
            // 否则创建新的私聊窗口
            PrivateChatUI newWindow = new PrivateChatUI(socket, myNickname, otherNickname);
            newWindow.setVisible(true);
            chatWindows.put(otherNickname, newWindow);
        }
    }

    public ChatRoomUI() {}

    public ChatRoomUI(String username, Socket socket) {
        log.info(username+"进入");
        ChatRoomUI.username = username;
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

        // 在构造函数中添加以下代码


        messageArea.addHyperlinkListener(new HyperlinkListener() {
    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                // 解析自定义协议
                URI uri = new URI(e.getDescription());
                if ("filedown".equals(uri.getScheme())) {
                    String[] parts = uri.getRawSchemeSpecificPart().split("\\?", 2);
                    byte[] fileData = Base64.getDecoder().decode(parts[0]);
                    String fileName = URLDecoder.decode(parts.length > 1 ? parts[1].replace("name=", "") : "", "UTF-8");
                    downloadFile(fileName, fileData);
                } else {
                    log.error("无效的链接协议: " + uri.getScheme());
                }
            } catch (URISyntaxException | UnsupportedEncodingException ex) {
                log.error("解析链接时出现问题: " + ex.getMessage(), ex);
            }
        }
    }
});
         addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 发送断开连接的消息
                sendDisconnectMessage();
                // 关闭窗口
                dispose();
            }
        });






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
        userList.setBackground(Color.decode("#b8860b"));
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

        // 创建文件选择按钮
        JButton fileButton = createButton("选择文件", Color.decode("#007AFF"), event -> chooseFile());

        // 创建消息输入面板
        inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBackground(Color.decode("#FFFFFF"));
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(fileButton, BorderLayout.WEST);
        inputPanel.add(sendButton, BorderLayout.EAST);


        // 将组件添加到窗口
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(userScrollPane, BorderLayout.EAST); // 将用户列表添加到右侧

        // 设置窗口背景颜色
        getContentPane().setBackground(Color.decode("#F5F5F5"));

    }

    //客户端退出消息
    private void sendDisconnectMessage(){
        Message mes = new Message();
        mes.setMessageType(MessageType.MESSAGE_EXIT_MES);
        mes.setSender(username);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(mes);
            oos.flush();
        } catch (IOException e) {
            log.error("关闭窗口时发生错误{}",e);
            throw new RuntimeException(e);
        }


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

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            sendFile(selectedFile);
        }
    }

    private void sendFile(File selectedFile) {
        try {
            FileInputStream fis =  new FileInputStream(selectedFile);
            byte[] data = Utils.StreamToByte(fis);
            Message mes=new Message();
            mes.setData(data);
            mes.setSender(username);
            mes.setFileName(selectedFile.getName());
            mes.setMessageType(MessageType.MESSAGE_SEND_FILE);
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            mes.setDate(now.format(formatter)); // 格式化日期时间
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(mes);
            oos.flush();
        } catch (IOException e) {
            log.error("发送文件出现错误"+e);
            throw new RuntimeException(e);
        }

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
        //难度需要向服务端请求？？？
        mes.setReceiver("allpeople");
        mes.setMessageType(MessageType.MESSAGE_ALL_MES);
        mes.setContent(message);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        mes.setDate(now.format(formatter)); // 格式化日期时间
        // 发送给服务端返回
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(mes);
            oos.flush();
        } catch (IOException e) {
            log.info("客户端发送消息出现问题{}", e);
            throw new RuntimeException(e);
        }
        // 这里需要读服务端发送的消息
        messageField.setText("");
    }

    public static void update(Message mes) {
        if (mes != null) {
            if(mes.getMessageType().equals(MessageType.MESSAGE_SHOW_USER)){
                log.debug("消息类型为{}",mes.getMessageType());
                return;
            }
                   User user = mes.getUser();
        String avatarPath;

        // 先验证一下路径是否存在
        if (user == null || user.getImage() == null) {
            avatarPath = "/孙.jpg";
        } else {
            String image = user.getImage();
            File imageFile = new File(image);
            if (imageFile.exists()) {
                avatarPath = image; // 获取头像路径
            } else {
                avatarPath = "/孙.jpg";
            }
        }

        log.debug("图片路径{}", avatarPath);
        String content = mes.getContent();
        String date = mes.getDate();
        String sender = mes.getSender();

        // 构建HTML内容
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<div style='display: flex; align-items: flex-end;'>");
        htmlContent.append("<span style='font-size:10px;'>").append(date).append("</span><br>");
        boolean isMyMessage = sender.equals(username);

        if (isMyMessage) {
            htmlContent.append("<div style='margin-left: auto; background-color: #DCF8C6; padding: 10px; border-radius: 10px;'>");
        } else {
            htmlContent.append("<div style='background-color: #FFFFFF; padding: 10px; border-radius: 10px;'>");
        }

        if (avatarPath != null && !avatarPath.isEmpty()) {
            File imageFile = new File(avatarPath);
            if (imageFile.exists()) {
                htmlContent.append("<img src='file:").append(imageFile.getAbsolutePath().replace("\\", "/")).append("' width='30' height='30' style='margin-right: 10px;'>");
            } else {
                // 使用类路径资源
                URL resourceUrl = ChatRoomUI.class.getResource(avatarPath);
                if (resourceUrl != null) {
                    htmlContent.append("<img src='").append(resourceUrl.toExternalForm()).append("' width='30' height='30' style='margin-right: 10px;'>");
                } else {
                    htmlContent.append("[图片不存在] ");
                    log.warn("图片文件不存在: " + avatarPath);
                }
            }
        } else {
            htmlContent.append("[无头像] ");
        }


            if (mes.getMessageType() == MessageType.MESSAGE_SEND_FILE) {
                String fileName = mes.getFileName()!=null?mes.getFileName():"未知文件";
                byte[] fileData = mes.getData();
                if (fileData == null || fileData.length == 0) {
                    log.error("文件数据为空，无法生成文件链接");
                    htmlContent.append("<b>").append(sender).append("</b>: [文件数据为空]").append("</div></div>");
                } else {
                    log.info("文件生成成功！");
                    // 使用自定义协议来代替 javascript:
                    String fileLink = null;
                    try {
                        fileLink = "<a href='filedown:" + Base64.getEncoder().encodeToString(fileData) + "?name=" + URLEncoder.encode(fileName, "UTF-8") + "'>" + fileName + "</a>";
                    } catch (UnsupportedEncodingException e) {
                        log.error("生成文件出现错误{}",e);
                        throw new RuntimeException(e);
                    }
                    htmlContent.append("<b>").append(sender).append("</b>: ").append(fileLink).append("</div></div>");
                }
            }else{
                htmlContent.append("<b>").append(sender).append("</b>: ").append(content).append("</div></div>");
            }

            // 插入到消息区域
            try {
                HTMLEditorKit kit = (HTMLEditorKit) messageArea.getEditorKit();
                HTMLDocument doc = (HTMLDocument) messageArea.getDocument();
                int docLength = doc.getLength(); // 获取文档的实际长度
                kit.insertHTML(doc, docLength, htmlContent.toString(), 0, 0, null);
                messageArea.setCaretPosition(doc.getLength());
            } catch (BadLocationException | IOException e) {
                log.error("插入消息时出现问题: " + e.getMessage(), e);
            }
    } else {
        log.error("message是null！");
    }
}


   public void downloadFile(String fileName, byte[] fileData) {
        try {
            File defaultDownloadDir = new File(System.getProperty("user.home") + "/Downloads");
            if (!defaultDownloadDir.exists()) {
                defaultDownloadDir.mkdirs();
            }
            File destFile = new File(defaultDownloadDir, fileName);
            FileOutputStream fos = new FileOutputStream(destFile);
            fos.write(fileData);
            fos.close();
            log.info("文件已下载到: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("下载文件时出现问题: " + e.getMessage(), e);
        }
    }


    public static ChatRoomUI getInstance() {
        if (instance == null) {
            instance = new ChatRoomUI();
        }
        return instance;
    }











    // 获取用户信息的线程
   public static void get(Message message) {
       SwingUtilities.invokeLater(() -> {
           userListModel.clear();
           String[] split = message.getContent().split(",");
           for (String user : split) {
               addUser(user);
           }
       });
   }


    // 添加用户的方法
    private static void addUser(String name) {
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
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public Socket getSocket() {
        return socket;
    }
}

// 用户列表鼠标监听器类
class UserListMouseListener extends MouseAdapter {
    private static final Logger log =LoggerFactory.getLogger(UserListMouseListener.class);
    private ChatRoomUI chatRoomUI;
    private final String username;
    private Socket socket;

    public UserListMouseListener(ChatRoomUI chatRoomUI, String username) {
        this.chatRoomUI = chatRoomUI;
        this.username = username;
        socket = chatRoomUI.getSocket();
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        if (evt.getClickCount() == 2) { // 双击事件
            int index = chatRoomUI.userList.locationToIndex(evt.getPoint());
            String selectedUser = chatRoomUI.userListModel.get(index);
            showUserOptions(selectedUser);

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
            userData(selectedUser);
            //发送请求给服务端，返回用户信息

        } else if (choice == 1) {
            new PrivateChatUI(socket,username, selectedUser).setVisible(true);
            openprivateChatRoom();
            //这里发送消息告诉服务器客户端打开私聊，检查留言！
        }
    }

    private void userData(String selectedUser) {
        Message msg =new Message();
        msg.setSender(selectedUser);
        msg.setMessageType(MessageType.MESSAGE_USER_DATA);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(msg);
            oos.flush();
            log.info("发送用户信息请求成功{}",msg);
        } catch (IOException e) {
            log.error("请求用户信息失败");
            System.exit(1);
        }

    }

    private void openprivateChatRoom(){
        try {
            Message message = new Message();
            message.setMessageType(MessageType.MESSAGE_OPEN_MES);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            oos.flush();
            log.info("私聊窗口打开消息成功！");
        } catch (IOException e) {
            log.error("客户端发送私聊窗口错误{e}",e);
            System.exit(1);
        }
    }
    //注册

}
