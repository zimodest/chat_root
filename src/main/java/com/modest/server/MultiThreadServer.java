package com.modest.server;

import com.modest.util.CommUtils;
import com.modest.vo.MessageVo;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * description
 *
 * @author modest
 * @date 2019/08/18
 */
public class MultiThreadServer {

    private static final String IP;

    private static final int PORT;

    private static Map<String, Socket> clients = new ConcurrentHashMap<>();

    private static Map<String,Set<String>> groups = new ConcurrentHashMap<>();

    static {
        Properties properties = CommUtils.loadProperties("socket.properties");
        IP = properties.getProperty("address");
        PORT = Integer.parseInt(properties.getProperty("port"));
    }

    private static class ExecuteClient implements Runnable {

        private Socket client;

        private Scanner in;

        private PrintStream out;

        public ExecuteClient(Socket client) {
            this.client = client;

            try {
                this.in = new Scanner(client.getInputStream()) ;
                this.out = new PrintStream(client.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true) {
                if(in.hasNextLine()) {
                    String jsonStrFrom = in.nextLine();
                    MessageVo msgFromClient = (MessageVo)CommUtils.json2Object(jsonStrFrom, MessageVo.class);

                    if(msgFromClient.getType().equals("1")) {
                        String username = msgFromClient.getContent();

                        MessageVo msg2Client = new MessageVo();
                        msg2Client.setType("1");
                        msg2Client.setContent(CommUtils.object2Json(clients.keySet()));

                        out.println(CommUtils.object2Json(msg2Client));
                        sendUserLogin("newLogin:"+username);
                        clients.put(username,client);
                        System.out.println(username+"上线了");
                        System.out.println("当前聊天室共有"+clients.size());
                    } else if(msgFromClient.getType().equals("2")) {
                        String friendName = msgFromClient.getTo();
                        Socket socket = clients.get(friendName);

                        try {
                            PrintStream out = new PrintStream(socket.getOutputStream());
                            MessageVo msg2Client = new MessageVo();
                            msg2Client.setType("2");
                            msg2Client.setContent(msgFromClient.getContent());
                            System.out.println("收到私聊信息，内容为"+msgFromClient.getContent());
                            out.println(CommUtils.object2Json(msg2Client));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if(msgFromClient.getType().equals("3")) {
                        String groupName = msgFromClient.getContent();

                        Set<String> friends = (Set<String>) CommUtils.json2Object(msgFromClient.getTo(), Set.class);
                        groups.put(groupName,friends);
                        System.out.println("有新的群注册成功,群名称为"+
                                groupName+",一共有"+groups.size() + "个群");
                    } else if(msgFromClient.getType().equals("4")) {
                        System.out.println("服务器收到的群聊信息为"+msgFromClient);
                        String groupName = msgFromClient.getTo();
                        Set<String> names = groups.get(groupName);
                        Iterator<String> iterator = names.iterator();
                        while(iterator.hasNext()) {
                            String socketName = iterator.next();
                            Socket socket = clients.get(socketName);

                            try {
                                PrintStream printStream = new PrintStream(client.getOutputStream(),true, "UTF-8");
                                MessageVo messageVO = new MessageVo();
                                messageVO.setType("4");
                                messageVO.setContent(msgFromClient.getContent());
                                messageVO.setTo(groupName+"-"+CommUtils.object2Json(names));
                                out.println(CommUtils.object2Json(messageVO));
                                System.out.println("服务端收到的群聊信息为"+messageVO);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }

                    }

                }
            }
        }

        private void sendUserLogin(String msg) {
            for(Map.Entry<String, Socket> entry : clients.entrySet()) {
                Socket socket = entry.getValue();
                try {
                    PrintStream out = new PrintStream(socket.getOutputStream());
                    out.println(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        public static void main(String[] args) throws IOException {
            ServerSocket serverSocket = new ServerSocket(PORT);
            ExecutorService executorService = Executors.newFixedThreadPool(50);
            for(int i=0; i<50; i++) {
                System.out.println("等待客户端连接...");
                Socket client = serverSocket.accept();
                System.out.println("有新的连接，端口号为"+client.getPort());
                executorService.submit(new ExecuteClient(client));
            }
        }
    }
}
