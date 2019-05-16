package again._4routing;

import com.rabbitmq.client.*;

import java.io.IOException;

public class ReceiveLogsDirect2 {
    private static final String EXCHANGE_NAME = "direct_logs";
    // 路由关键字
    private static final String[] routingKeys = new String[]{"error"};

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.150.129");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        String queueName = channel.queueDeclare().getQueue();

        // 根据路由关键字进行多重绑定
        for (String security : routingKeys) {
            channel.queueBind(queueName, EXCHANGE_NAME, security);
            System.out.println("ReceiveLogsDirect2 exchange:"+EXCHANGE_NAME+", queue:"+queueName+", BindRoutingKey:" + security);
            System.out.println("ReceiveLogsDirect2 [*] Waiting for messages. To exit press CTRL+C");
        }

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}
