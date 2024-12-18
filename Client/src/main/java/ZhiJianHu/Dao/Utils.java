package ZhiJianHu.Dao;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/14
 * 说明:连接数据库
 */
public class Utils implements Serializable {
    private static final long serialVersionUID = 1L;


    public static byte[] StreamToByte(InputStream is) throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len;
            while((len=is.read(b))!=-1){
                out.write(b,0,len);
            }
            byte[] bytes = out.toByteArray();
            out.close();
            return bytes;
    }
   public static String StreamToString(InputStream in) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while((len=in.read(buffer))!=-1){
            return new String(buffer,0,len);
        }
        return null;
   }


}

