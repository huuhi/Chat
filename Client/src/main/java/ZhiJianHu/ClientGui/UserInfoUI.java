package ZhiJianHu.ClientGui;

import ZhiJianHu.Common.User;
import ZhiJianHu.Dao.UserDao;

import javax.swing.*;
import java.awt.*;

public class UserInfoUI extends JFrame {

    private JLabel avatarLabel;
    private JLabel nameLabel;
    private JLabel ageLabel;
    private JLabel genderLabel;
    private JLabel hobbiesLabel;
    private UserDao ud=new UserDao();

    public UserInfoUI(String username) {
        username=username.replace("(在线)","");
        //通过名字获得用户信息
        User user = ud.getUser(username);
        setTitle("用户信息 - " + username);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 创建面板来放置用户信息组件
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(5, 2));
        infoPanel.setBackground(Color.decode("#FFFFFF"));

        // 添加用户信息组件

        // 添加用户信息组件
        infoPanel.add(new JLabel("头像:"));
        String avatarPath = user.getImage(); // 获取头像路径
        if (avatarPath != null && !avatarPath.isEmpty()) {
            ImageIcon avatarIcon = new ImageIcon(avatarPath);
            if (avatarIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image image = avatarIcon.getImage();
                Image scaledInstance = image.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                avatarLabel = new JLabel(new ImageIcon(scaledInstance));
            } else {
                avatarLabel = new JLabel("头像加载失败");
            }
        } else {
            avatarLabel = new JLabel("无头像");
        }
        infoPanel.add(avatarLabel);

        infoPanel.add(new JLabel("姓名:"));
        nameLabel = new JLabel(username);
        infoPanel.add(nameLabel);

        infoPanel.add(new JLabel("年龄:"));
        ageLabel = new JLabel(String.valueOf(user.getAge())); // 示例年龄
        infoPanel.add(ageLabel);

        infoPanel.add(new JLabel("性别:"));
        genderLabel = new JLabel(user.getSex()); // 示例性别
        infoPanel.add(genderLabel);

        infoPanel.add(new JLabel("爱好:"));
        hobbiesLabel = new JLabel(user.getHobbies()); // 示例爱好
        infoPanel.add(hobbiesLabel);

        // 将面板添加到窗口
        add(infoPanel, BorderLayout.CENTER);

        // 设置窗口背景颜色
        getContentPane().setBackground(Color.decode("#F5F5F5"));
    }
}