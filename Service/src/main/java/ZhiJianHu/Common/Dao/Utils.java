package ZhiJianHu.Common.Dao;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.Serializable;
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
    private static DataSource dataSource;
    static{
        Properties p=new Properties();
        try {
            p.load(new FileInputStream(Resource.class.getResource("/druid.properties").getFile()));
            dataSource= DruidDataSourceFactory.createDataSource(p);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Connection con(){
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //关闭
    public static void closeCon(Connection con){
        if(con!=null){
            try {
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


}

