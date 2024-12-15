package ZhiJianHu.Homework3;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/14
 * 说明:
 */
@Data
public class Student {
     private int id;
     private String name;
     private String sex;
     private LocalDateTime time;
     private String place;

     public Student(int id, String name, String sex, LocalDateTime time, String place) {
          this.id = id;
          this.name = name;
          this.sex = sex;
          this.time = time;
          this.place = place;
     }
}
