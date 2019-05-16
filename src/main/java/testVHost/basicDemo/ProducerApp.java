package testVHost.basicDemo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ProducerApp {
    public static void main(String[] args) throws IOException, TimeoutException{
        Connection connection = null;
        Channel channel = null;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("192.168.150.129");
            factory.setPort(5672);
            factory.setUsername("rabbitmq_producer");
            factory.setPassword("123456");
            factory.setVirtualHost("test_vhosts");

            // 创建与RabbitMQ服务器的TCP连接
            connection = factory.newConnection();
            channel = connection.createChannel();

            // Queue.DeclareOk queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments) throws IOException;
            // 这个方法的第二个参数durable表示建立的消息队列是否是持久化(RabbitMQ重启后仍然存在,并不是指消息的持久化),
            // 第三个参数exclusive 表示建立的消息队列是否只适用于当前TCP连接，
            // 第四个参数autoDelete表示当队列不再被使用时，RabbitMQ是否可以自动删除这个队列。
            // 第五个参数arguments定义了队列的一些参数信息，主要用于Headers Exchange进行消息匹配时。
            /* VERSION 1
            channel.queueDeclare("firstQueue", true, false, false, null);
            String message = "First Message";
             */

            // Exchange.DeclareOk exchangeDeclare(String exchange, String type，boolean durable) throws IOException
            // exchangeDeclare方法的第一个参数exchange是exchange名称，
            // 第二个参数type是Exchange类型，有“direct”,“fanout”,“topic”,“headers”四种,分别对应RabbitMQ的四种Exchange。
            // 第三个参数durable是设置Exchange是否持久化( 即在RabbitMQ服务器重启后Exchange是否仍存在，如果没有设置，默认是非持久化的)
            channel.exchangeDeclare("directExchange", "direct");
            channel.queueDeclare("directQueue", true, false, false, null);

            // Queue.BindOk queueBind(String queue, String exchange, String routingKey) throws IOException;
            // queueBind方法第一个参数queue是消息队列的名称，
            // 第二个参数exchange是Exchange的名称，
            // 第三个参数routingKey是消息队列和Exchange之间绑定的路由key，我们这里绑定的路由key是“directMessage”。
            // 从Exchange过来的消息，只有routing key为“directMessage”的消息会被转到消息队列“directQueue”，其他消息将不会被转发
            channel.queueBind("directQueue", "directExchange", "directMessage");
            String message = "First Indirect Message";

            // void basicPublish(String exchange, String routingKey, BasicProperties props, byte[] body) throws IOException;
            // 第一个参数exchange是消息发送的Exchange名称，如果没有指定，则使用Default Exchange。
            // 第二个参数routingKey是消息的路由Key，是用于Exchange将消息路由到指定的消息队列时使用(如果Exchange是Fanout Exchange，这个参数会被忽略),
            // 第三个参数props是消息包含的属性信息。
            /* VERSION 1
            channel.basicPublish("", "firstQueue", null, message.getBytes());
            System.out.println("Send Message is '" + message + "'");
             */
            channel.basicPublish("directExchange", "indirectMessage", null, message.getBytes());
            System.out.println("Send Indirect Message is:'" + message  + "'");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (channel != null)
                channel.close();
            if (connection != null)
                connection.close();
        }
    }
}
