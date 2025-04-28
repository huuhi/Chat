package ZhiJianHu;

import ZhiJianHu.ClientGui.LoginRegisterUI;

import javax.swing.*;


public class ChatApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginRegisterUI().setVisible(true); // 创建并显示登录/注册界面
        });
    }
}