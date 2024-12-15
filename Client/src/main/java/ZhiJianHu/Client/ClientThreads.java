package ZhiJianHu.Client;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 胡志坚
 * @version 1.0
 * 创造日期 2024/12/15
 * 说明:
 */
public class ClientThreads {
    private static Map<String,ClientConnectServiceThread> mp=new HashMap<>();

    public static void addClientConnectServiceThread(String name,ClientConnectServiceThread ccst){
        mp.put(name,ccst);
    }
}
