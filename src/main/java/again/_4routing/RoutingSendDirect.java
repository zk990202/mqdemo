package again._4routing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RoutingSendDirect {
    private static final String EXCHANGE_NAME = "direct_logs";
    // 路由关键字
    private static final String[] routingKeys = new String[]{"info", "warning", "error"};

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.150.129");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        // 发送消息
        for (String security : routingKeys){
            String message = "Send the message level: " + security;
            channel.basicPublish(EXCHANGE_NAME, security, null, message.getBytes());
            System.out.println(" [x] Sent '" + security + "':'" + message + "'");
        }

        channel.close();
        connection.close();
    }
}
