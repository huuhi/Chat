package ZhiJianHu.ClientGui;

import ZhiJianHu.Common.Message;
import ZhiJianHu.Common.MessageType;
import ZhiJianHu.Common.User;
import ZhiJianHu.Dao.UserDao;
import ZhiJianHu.Dao.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class PrivateChatUI extends JFrame implements KeyListener {

    private static final Logger log = LoggerFactory.getLogger(ChatRoomUI.class);
    private static JTextPane messageArea; // 消息显示区
    private JTextField messageField; // 消息输入框
    private JButton sendButton; // 发送按钮
    private static String myNickname; // 我的昵称
    private String otherNickname; // 对方的昵称
    private Socket socket;
    private static UserDao ud=new UserDao();

    public PrivateChatUI(Socket socket,String myNickname, String otherNickname) {
        log.info(myNickname+"与"+otherNickname);
        System.out.println(myNickname+otherNickname);
        this.socket=socket;
        PrivateChatUI.myNickname = myNickname;
        this.otherNickname = otherNickname;
        setTitle("私聊 - " + myNickname + " 和 " + otherNickname);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 创建消息显示区
// 创建消息显示区

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
        sendButton = createButton("发送", Color.decode("#007AFF"), event -> sendMessage());

        // 创建消息输入面板
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBackground(Color.decode("#FFFFFF"));
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        JButton fileButton = createButton("选择文件", Color.decode("#007AFF"), event -> chooseFile());

        // 创建消息输入面板
        inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBackground(Color.decode("#FFFFFF"));
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(fileButton, BorderLayout.WEST);
        inputPanel.add(sendButton, BorderLayout.EAST);


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




        // 将组件添加到窗口
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        // 设置窗口背景颜色
        getContentPane().setBackground(Color.decode("#F5F5F5"));
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
            mes.setSender(myNickname);
            mes.setReceiver(otherNickname);
            mes.setFileName(selectedFile.getName());
            mes.setMessageType(MessageType.MESSAGE_PRIVATE_FILE);
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            mes.setDate(now.format(formatter)); // 格式化日期时间
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(mes);
            oos.flush();
            updateMes(mes);
        } catch (IOException e) {
            log.error("发送文件出现错误"+e);
            throw new RuntimeException(e);
        }

    }




    public static void updateMes(Message mes) {
        //更新信息
        if (mes != null) {
            User user = ud.getUser(mes.getSender());
            String avatarPath = user.getImage(); // 获取头像路径
            String content = mes.getContent();
            String date = mes.getDate().toString();
            String sender = mes.getSender();

            // 构建HTML内容
            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<div style='display: flex; align-items: flex-end;'>");
            htmlContent.append("<span style='font-size:10px;'>").append(date).append("</span><br>");
            boolean isMyMessage = sender.equals(myNickname);

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
                    htmlContent.append("[图片不存在] ");
                    log.warn("图片文件不存在: " + avatarPath);
                }
            } else {
                htmlContent.append("[无头像] ");
            }

            if (mes.getMessageType() == MessageType.MESSAGE_PRIVATE_FILE) {
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
        //发送私聊信息
        String message = messageField.getText();
        if (!message.trim().isEmpty()&&socket!=null) {
            if(socket.isClosed()){
                log.error("socket寄了");
            }
            Message mes = new Message();
            mes.setContent(message);
            mes.setSender(myNickname);
            mes.setReceiver(otherNickname);
            mes.setMessageType(MessageType.MESSAGE_PRIVATE_MES);
            LocalDateTime now=LocalDateTime.now();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            mes.setDate(now.format(df));
            try {
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(mes);
                oos.flush();
            } catch (IOException e) {
                log.error("发送私聊信息出现问题"+e);
                throw new RuntimeException(e);
            }
            messageField.setText("");
            updateMes(mes);
        }else{
            log.info("信息为空或者线程寄了");
        }
    }
    //更新消息面板
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

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if(keyCode == KeyEvent.VK_ENTER){
            sendMessage();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}