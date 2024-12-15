package ZhiJianHu.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/15
 * 说明:存储线程，方便管理
 */
public class ServiceThreads {
    private static Map<String,ServiceThread> mp=new HashMap<>();

    //添加线程
    public static void addServiceThread(String threadName,ServiceThread st){
        mp.put(threadName,st);
    }

    public static Map<String, ServiceThread> get(){
        return new HashMap<>(mp);
    }
    public static ServiceThread getThread(String threadName) {
        return mp.get(threadName);
    }

    public static void removeConnection(String threadName) {
        mp.remove(threadName);
    }
    public static boolean contains(String name){
        return mp.containsKey(name);
    }
}
