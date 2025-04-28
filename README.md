# Chat

这是使用JavaIO网络通信编写的聊天系统，数据存储方式：Mysql，传输文件大小：小于10MB

## Client客户端

客户端内容：


各个模块的主要功能

- Client：与服务端通信，请求数据，开启线程

- ClientGui：客户端页面，写的比较敷衍

- Common：公共类，服务端也有一份

- Dao：本来我是用来跟MySQL交互的，后面发现应该分离开，现在已经改名Utils,；里面存放工具类，读取文件，转换成字节数组

  #### 类的具体功能




ClientConnectServiceThread


开启客户端线程，负责接收服务端消息，然后处理返回的消息


ClientThreads


存储用户线程，我发现没有什么用，删了不影响启动


UserClientService


负责发送：开启线程，连接服务端，发送登录和注册请求以及用户列表

GUI我就不解释了



Message类存储消息，MessageType消息类型，User用户信息，ChatApp启动类

主要功能：实现了留言，私聊，群聊，实时更新用户在线消息，点击用户名称可查看基本信息

可扩展功能：添加添加好友功能，添加创造群聊功能







### Service服务端

服务端功能：等待客户端连接，然后开启线程与该客户端通信

![image-20250105133446179](C:\Users\windows\AppData\Roaming\Typora\typora-user-images\image-20250105133446179.png)

各个模块的主要功能：

- Common：跟客户端一样，公共类

- Dao：负责操作数据库，得到数据给服务端发送给客户端

- Service：处理客户端发送的请求，并返回数据

  ![image-20250105133731111](C:\Users\windows\AppData\Roaming\Typora\typora-user-images\image-20250105133731111.png)

- BasicDao:基础类，其他类继承这个类，实现各自特有功能

- Message_Dao:负责处理信息

- UserDao：负责处理用户信息
- Utils:工具类，连接数据库，改变连接，转换字节数组
- Leave_Mes:处理留言消息
- Service：等待客户端连接，并且处理登录，注册请求，返回结果，如果登录成功开启线程与客户端通信，并且发送成功消息
- ServiceGUI：客户端界面
- ServiceThread：服务端线程
- ServiceThreads:管理服务端线程，这个可不能删除！

### ----------------1月5号更新-------------

新增功能：添加群聊功能

