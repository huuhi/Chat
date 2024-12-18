package ZhiJianHu.ClientGui;

import ZhiJianHu.Client.UserClientService;

import ZhiJianHu.Common.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;


public class LoginRegisterUI extends JFrame {

    private JTextField nameField; // 用户名输入框
    private JPasswordField passwordField; // 密码输入框
    private JButton loginButton, registerButton; // 登录和注册按钮

    private  Socket socket;
    private UserClientService ucs;

    public LoginRegisterUI() {
        this.socket=ucs.getSocket();
        setTitle("登录/注册");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中窗口
        setLayout(new BorderLayout());

        // 创建面板来放置标签、输入框和按钮
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));
        inputPanel.setBackground(Color.decode("#FFFFFF")); // 白色背景

        // 用户名和密码标签及输入框
        JLabel nameLabel = new JLabel("用户名:");
        JLabel passwordLabel = new JLabel("密码:");

        nameField = new JTextField();
        passwordField = new JPasswordField();

        // 设置字体和颜色
        Font labelFont = new Font("KaiTi", Font.PLAIN, 16);
        nameLabel.setFont(labelFont);
        passwordLabel.setFont(labelFont);
        nameField.setFont(labelFont);
        passwordField.setFont(labelFont);

        // 添加到面板
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);

        // 创建登录和注册按钮
        loginButton = createButton("登录", Color.decode("#007AFF"), event -> login());
        registerButton = createButton("注册", Color.decode("#4CD964"), event -> register());

        // 添加到面板
        inputPanel.add(loginButton);
        inputPanel.add(registerButton);

        // 将面板添加到窗口
        add(inputPanel, BorderLayout.CENTER);

        // 设置窗口背景颜色
        getContentPane().setBackground(Color.decode("#F5F5F5")); // 浅灰色背景
    }

    // 创建按钮的方法
    private JButton createButton(String text, Color background, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("KaiTi", Font.BOLD, 16)); // 楷体，加粗样式，大小16
        button.setBackground(background); // 按钮背景颜色
        button.setForeground(Color.WHITE); // 按钮文字颜色
        button.setBorderPainted(false); // 去掉边框
        button.addActionListener(listener); // 添加监听器
        return button;
    }

    // 登录方法
    private void login() {
        String name = nameField.getText();
        // 假设这是 LoginRegisterUI.java 文件中的第 85 行
        String password = new String(passwordField.getPassword());

        // 这里可以添加登录逻辑
        //登录lj
        //需要发送消息给服务端，然后验证是否正确

        //一旦有人登录成功就请求用户列表
        if(new UserClientService().isLogin(name,password)){
            //JOptionPane.showMessageDialog(this, "登录成功!"); // 示例提示
            dispose(); // 关闭当前窗口
            UserClientService.getUser();

//            new ChatRoomUI(name,socket).setVisible(true); // 打开聊天室界面
        }else{
            JOptionPane.showMessageDialog(this, "登陆失败！");
        }

    }


    // 注册方法
    private void register() {
        // 弹出注册对话框
        RegisterDialog dialog = new RegisterDialog(this, true);
        dialog.setVisible(true);
    }
}

// 注册对话框类


// 注册对话框类
class RegisterDialog extends JDialog {

    private JTextField nameField, ageField;
    private JComboBox<String> genderBox;
    private JTextArea hobbiesArea;
    private JButton selectAvatarButton;
    private JLabel avatarLabel;
    private BufferedImage avatarImage; // 保存选择的头像
    private ImageIcon icon;
    String  image=null;

    private JPanel registerPanel;

public RegisterDialog(Frame owner, boolean modal) {
    super(owner, modal);
    setTitle("注册");
    setSize(400, 400);
    setLocationRelativeTo(owner);
    setLayout(new BorderLayout());

    registerPanel = new JPanel();
    // 创建面板来放置注册组件
    registerPanel.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5); // 组件之间的间距

    // 添加注册组件
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0.0;
    gbc.weighty = 0.0;
    registerPanel.add(new JLabel("选择头像:"), gbc);

    avatarLabel = new JLabel("无", SwingConstants.CENTER); // 居中对齐
    avatarLabel.setBorder(BorderFactory.createLineBorder(Color.decode("#CCCCCC"), 1)); // 添加边框
    avatarLabel.setPreferredSize(new Dimension(50, 50)); // 设置大小
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 1.0;
    gbc.weighty = 0.0;
    registerPanel.add(avatarLabel, gbc); // 确保先添加 avatarLabel

    selectAvatarButton = new JButton("选择...");
    selectAvatarButton.addActionListener(event -> {
        String selectedImagePath = selectAvatar(); // 调用选择头像的方法并获取路径
        if (!selectedImagePath.isEmpty()) {
            this.image = selectedImagePath; // 更新类级别的image变量
            repaint();
        }else{
            this.image = "/孙.jpg";
        }
    });
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    gbc.weighty = 0.0;
    registerPanel.add(selectAvatarButton, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0.0;
    gbc.weighty = 0.0;
    registerPanel.add(new JLabel("用户名:"), gbc);

    nameField = new JTextField();
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    gbc.weighty = 0.0;
    registerPanel.add(nameField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0.0;
    gbc.weighty = 0.0;
    registerPanel.add(new JLabel("密码:"), gbc);

    JPasswordField passwordField = new JPasswordField();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    gbc.weighty = 0.0;
    registerPanel.add(passwordField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0.0;
    gbc.weighty = 0.0;
    registerPanel.add(new JLabel("年龄:"), gbc);

    ageField = new JTextField();
    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    gbc.weighty = 0.0;
    registerPanel.add(ageField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0.0;
    gbc.weighty = 0.0;
    registerPanel.add(new JLabel("性别:"), gbc);

    genderBox = new JComboBox<>(new String[]{"男", "女"});
    gbc.gridx = 1;
    gbc.gridy = 5;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    gbc.weighty = 0.0;
    registerPanel.add(genderBox, gbc);

    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0.0;
    gbc.weighty = 0.0;
    registerPanel.add(new JLabel("爱好:"), gbc);

    hobbiesArea = new JTextArea();
    gbc.gridx = 1;
    gbc.gridy = 6;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    gbc.weighty = 0.0;
    registerPanel.add(hobbiesArea, gbc);

    // 添加到对话框
    add(registerPanel, BorderLayout.CENTER);

    // 创建确认按钮
    JButton confirmButton = new JButton("确认");
    confirmButton.addActionListener(event -> {
        String name = nameField.getText();
        String password = new String(passwordField.getPassword());
        String ageText = ageField.getText();
        String sex = (String) genderBox.getSelectedItem();
        String hobbies = hobbiesArea.getText();

        // 验证年龄输入
        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的年龄", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 确定完成添加到数据库
        User user = new User(name, password, sex, age, hobbies, image);
        boolean reg =new UserClientService().register(user);
        if (reg) {
            //UserClientService.getUser();
                JOptionPane.showMessageDialog(this, "注册成功!");
                dispose(); // 关闭对话框

        } else {
            JOptionPane.showMessageDialog(this, "注册失败!名字重复");
        }

    });
    gbc.gridx = 0;
    gbc.gridy = 7;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0.0;
    gbc.weighty = 0.0;
    add(confirmButton, BorderLayout.SOUTH);


    // 设置窗口背景颜色
    getContentPane().setBackground(Color.decode("#F5F5F5"));
}
//应发送注册请求给服务端，服务端判断



// 选择头像的方法
private String selectAvatar() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("选择头像图片");
    fileChooser.setFileFilter(new FileNameExtensionFilter("图像文件", "jpg", "jpeg", "png")); // 只允许选择图片文件

    int option = fileChooser.showOpenDialog(this);
    if (option == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        try {
            avatarImage = ImageIO.read(selectedFile); // 读取图片
            if (avatarImage != null) {
                // 调整图像大小以适应 JLabel 的大小
                Image scaledImage = avatarImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                icon = new ImageIcon(scaledImage);
                avatarLabel.setIcon(icon); // 设置图标
                avatarLabel.setText(""); // 清空文本

                // 强制重新绘制界面
                avatarLabel.revalidate();
                avatarLabel.repaint();

                // 输出日志，确保图像路径和加载成功

                // 确保父容器也重新绘制
                registerPanel.revalidate();
                registerPanel.repaint();
                // 确认 JLabel 的大小

                // 确保对话框也重新绘制
                revalidate();
                repaint();

                return selectedFile.getAbsolutePath(); // 返回图片路径
            } else {
                JOptionPane.showMessageDialog(this, "无法加载图像文件", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace(); // 输出详细的异常信息
            JOptionPane.showMessageDialog(this, "无法加载图像文件", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    return ""; // 如果没有选择或者加载失败则返回空字符串
}




}