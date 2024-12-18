package ZhiJianHu.Common;

import lombok.Data;

import java.io.Serializable;



/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/14
 * 说明:
 */
@Data
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String sender;
    private String receiver;
    private String content;
    private MessageType messageType;
    private String date;
    private byte[] data;
    private String fileName;
    private User user;

    public String getMessagetype() {
        return messageType.name();
    }
    public MessageType getMessageType() {
        return messageType;
    }


}
