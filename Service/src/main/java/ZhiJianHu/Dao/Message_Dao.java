package ZhiJianHu.Dao;

import ZhiJianHu.Common.Message;
import ZhiJianHu.Common.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.getConnection;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/17
 * 说明:操作留言表格
 */
public class Message_Dao extends  BasicDao<Message>{
    private static final Logger log= LoggerFactory.getLogger(Message_Dao.class);
    //加入消息
    public int addmes(Message mes) {
        String sql = "INSERT INTO leave_mes (sender, receiver, content, messageType, date, data, fileName) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            return update(sql, mes.getSender(), mes.getReceiver(),
                    mes.getContent(), mes.getMessagetype(), mes.getDate(), mes.getData(),
                    mes.getFileName());
        } catch (Exception e) {
            log.error("Error adding message: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    //根据接收人获得消息
        public List<Message> getmessage(String name) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM leave_mes WHERE receiver = ?";
        try (Connection conn = Utils.con();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Message message = new Message();
                message.setId(rs.getInt("id"));
                message.setSender(rs.getString("sender"));
                message.setReceiver(rs.getString("receiver"));
                message.setContent(rs.getString("content"));
                message.setMessageType(MessageType.valueOf(rs.getString("messageType"))); // 设置消息类型为字符串，自动转换为枚举
                message.setDate(String.valueOf(rs.getTimestamp("date")));
                message.setData(rs.getBytes("data"));
                message.setFileName(rs.getString("fileName"));
                messages.add(message);
            }
        } catch (SQLException e) {
            log.error("从数据库读取消息时出错: {}", e.getMessage(), e);
        }
        return messages;
    }
    public int removemes(int id){
        String sql="delete from leave_mes where id=?;";
        return update(sql,id);
    }


}
