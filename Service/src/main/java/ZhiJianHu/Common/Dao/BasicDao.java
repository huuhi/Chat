package ZhiJianHu.Common.Dao;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/14
 * 说明:基本Dao
 */
public class BasicDao<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(BasicDao.class);

    //增删改
     public int update(String sql, Object... params) {
         Connection con = Utils.con();
         QueryRunner qr = new QueryRunner();

            try {
                return qr.update(con, sql, params);
            } catch (SQLException e) {
                log.error("Database update error: {}", e.getMessage(), e);
                System.exit(1);
            }finally {
                Utils.closeCon(con);
            }
            return -1;
        }
    //查找多个数据
    public List<T> query(String sql,Class<T> cla,Object... params){
        Connection con = Utils.con();
        QueryRunner qr = new QueryRunner();
        try {
            return qr.query(con,sql,new BeanListHandler<>(cla),params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            Utils.closeCon(con);
        }
    }
    //单个数据
    public T querySingle(String sql, Class<T> cla,Object... params){
        Connection con = Utils.con();
        QueryRunner qr = new QueryRunner();
        try {
            return qr.query(con,sql,new BeanHandler<>(cla),params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            Utils.closeCon(con);
        }
    }
    //单个对象
    public Object query(String sql,Object... params){
        Connection con = Utils.con();
        QueryRunner qr = new QueryRunner();
        try {
            return qr.query(con,sql,new ScalarHandler<>(),params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            Utils.closeCon(con);
        }
    }
    public List<T> query(String sql,Class<T> cla){
        Connection con = Utils.con();
        QueryRunner qr = new QueryRunner();
        try {
            return qr.query(con,sql,new BeanListHandler<>(cla));
        } catch (SQLException e) {
            log.error("查询出现问题"+e);
            throw new RuntimeException(e);
        }finally {
            Utils.closeCon(con);
        }
    }





}
