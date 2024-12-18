import ZhiJianHu.Common.Dao.UserDao;
import ZhiJianHu.Common.User;
import ZhiJianHu.Dao.Utils;
import org.junit.Test;

import java.sql.Connection;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/14
 * 说明:
 */
public class UtilsTest {
     @Test
     public void test(){
          for (int i = 0; i <100 ; i++) {
               Connection con = Utils.con();
               Utils.closeCon(con);
          }
     }
     @Test
     public void testregister() {
          User u= new User("小明","123","男",18,"打游戏,看电影","111");
          UserDao userDao = new UserDao();
          int register = userDao.register(u);
          System.out.println(register);
     }

     @Test
     public void login() {
          String name="小明";
          String pwd="123";
          UserDao userDao = new UserDao();
          User login = userDao.login(name, pwd);
          System.out.println(login);
     }
}
