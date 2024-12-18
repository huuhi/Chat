package ZhiJianHu.Dao;

import ZhiJianHu.Common.User;

import java.io.Serializable;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/14
 * 说明:
 */
public class UserDao extends BasicDao<User> implements Serializable {
    private static final long serialVersionUID = 1L;
    //根据需求写方法
    public  User login(String username, String password) {
        //登录方法
        String sql="select * from user where name=? and pwd=md5(?);";
        return  querySingle(sql,User.class, username, password);
    }

    //注册方法
    public int register(User user){
        String sql="insert into user values(null,?,md5(?),?,?,?,?);";
        return update(sql,user.getName(),user.getPwd(),user.getSex(),
                user.getAge(),user.getHobbies(),user.getImage());
    }
    //获得用户信息
    public User getUser(String username){
        String sql="select * from user where name=?;";
        return querySingle(sql,User.class,username);
    }

}
