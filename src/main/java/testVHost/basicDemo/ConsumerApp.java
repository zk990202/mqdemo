package testVHost.basicDemo;

import com.rabbitmq.client.*;

import java.io.IOException;

public class ConsumerApp {
    public static void main(String[] args){
        Connection connection = null;
        Channel channel = null;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("192.168.150.129");
            factory.setPort(5672);
            factory.setUsername("rabbitmq_producer");
            factory.setPassword("123456");
            factory.setVirtualHost("test_vhosts");

            // 创建于RabbitMQ服务器的TCP连接
            connection = factory.newConnection();
            channel = connection.createChannel();

            Consumer consumer = new DefaultConsumer(channel){
                /*
                void handleDelivery(String consumerTag,
                                    Envelope envelope,
                                    AMQP.BasicProperties properties,
                                    byte[] body) */
                // handleDelivery方法的第一个参数consumerTag是接收到消息时的消费者Tag，如果我们没有在basicConsume方法中指定Consumer Tag，RabbitMQ将使用随机生成的Consumer Tag
                // 第二个参数envelope是消息的打包信息，包含了四个属性：
                // 1._deliveryTag，消息发送的编号，表示这条消息是RabbitMQ发送的第几条消息，我们可以看到这条消息是发送的 第一条消息。
                //
                //2._redeliver，重传标志，确认在收到对消息的失败确认后，是否需要重发这条消息，我们这里的值是false，不需要重发。
                //
                //3._exchange，消息发送到的Exchange名称，正如我们上面发送消息时一样，exchange名称为空，使用的是Default Exchange。
                //
                //4._routingKey,消息发送的路由Key，我们这里是发送消息时设置的“firstQueue”。
                // 第三个参数properties就是上面使用basicPublish方法发送消息时的props参数，由于我们上面设置它为null，这里接收到的properties 是默认的Properties，只有bodySize，其他全是null。
                // 第四个参数body是消息体.
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("Consumer has received '" + message + "'");
                }
            };

            // String basicConsume(String queue, boolean autoAck, Consumer callback) throws IOException
            // basicConsume方法的第一个参数是Consumer绑定的队列名，
            // 第二个参数是自动确认标志，如果为true，表示Consumer接受到消息后，会自动发确认消息(Ack消息)给消息队列，消息队列会将这条消息从消息队列里删除，
            // 第三个参数就是Consumer对象，用于处理接收到的消息。
            channel.basicConsume("firstQueue", true, consumer);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
