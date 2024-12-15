package ZhiJianHu.Common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/14
 * 说明:数据库！存储用户信息
 */
@Data
public class User implements Serializable {
     private static final long serialVersionUID = 1L;
     private int id;
     private String name;
     private String pwd;
     private String sex;
     private int age;
     private String hobbies;
     private String image;

     public User(String name, String password, String sex, int age, String hobbies,String image) {
          this.name = name;
          this.pwd = password;
          this.sex = sex;
          this.age = age;
          this.hobbies = hobbies;
          this.image=image;
     }

     public  User(){}
}
