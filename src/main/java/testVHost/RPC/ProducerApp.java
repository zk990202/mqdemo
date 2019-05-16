package testVHost.RPC;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.AMQImpl;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class ProducerApp {
    public static void main(String[] args) throws IOException, TimeoutException {
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

            // Queue.DeclareOk queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments) throws IOException;
            // 这个方法的第二个参数durable表示建立的消息队列是否是持久化(RabbitMQ重启后仍然存在,并不是指消息的持久化),
            // 第三个参数exclusive 表示建立的消息队列是否只适用于当前TCP连接，
            // 第四个参数autoDelete表示当队列不再被使用时，RabbitMQ是否可以自动删除这个队列。
            // 第五个参数arguments定义了队列的一些参数信息，主要用于Headers Exchange进行消息匹配时。
            /* VERSION 1
            channel.queueDeclare("firstQueue", true, false, false, null);
            String message = "First Message";
             */

            // //创建RPC发送消息的Direct Exchange,消息队列和绑定关系。
            // Exchange.DeclareOk exchangeDeclare(String exchange, String type，boolean durable) throws IOException
            // exchangeDeclare方法的第一个参数exchange是exchange名称，
            // 第二个参数type是Exchange类型，有“direct”,“fanout”,“topic”,“headers”四种,分别对应RabbitMQ的四种Exchange。
            // 第三个参数durable是设置Exchange是否持久化( 即在RabbitMQ服务器重启后Exchange是否仍存在，如果没有设置，默认是非持久化的)
            channel.exchangeDeclare("rpcSendExchange", "direct", true);
            channel.queueDeclare("rpcSendQueue", true, false, false, null);

            // Queue.BindOk queueBind(String queue, String exchange, String routingKey) throws IOException;
            // queueBind方法第一个参数queue是消息队列的名称，
            // 第二个参数exchange是Exchange的名称，
            // 第三个参数routingKey是消息队列和Exchange之间绑定的路由key，我们这里绑定的路由key是“directMessage”。
            // 从Exchange过来的消息，只有routing key为“directMessage”的消息会被转到消息队列“directQueue”，其他消息将不会被转发
            channel.queueBind("rpcSendQueue", "rpcSendExchange", "rpcSendMessage");

            // //建立RPC返回消息的Direct Exchange, 消息队列和绑定关系
            channel.exchangeDeclare("rpcReplyExchange", "direct", true);
            channel.queueDeclare("rpcReplyQueue", true, false, false, null);
            channel.queueBind("rpcReplyQueue", "rpcReplyExchange", "rpcReplyMessage");

            // //创建接收RPC返回消息的消费者，并将它与RPC返回消息队列相关联。
            DefaultConsumer replyCustomer = new DefaultConsumer(channel);
            channel.basicConsume("rpcReplyQueue", true, replyCustomer);

            String number = "10";

            // //生成RPC请求消息的CorrelationId
            final String correlationId = UUID.randomUUID().toString();

            //在RabbitMQ消息的Properties中设置RPC请求消息的CorrelationId以及
            //ReplyTo名称(我们这里使用的是Exchange名称，
            //而不是消息队列名称)
//            BasicProperties props = new BasicProperties
//                    .Builder()
//                    .correlationId(correlationId)
//                    .replyTo("rpcReplyExchange")
//                    .build();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(correlationId)
                    .replyTo("rpcReplyExchange")
                    .build();

            // void basicPublish(String exchange, String routingKey, BasicProperties props, byte[] body) throws IOException;
            // 第一个参数exchange是消息发送的Exchange名称，如果没有指定，则使用Default Exchange。
            // 第二个参数routingKey是消息的路由Key，是用于Exchange将消息路由到指定的消息队列时使用(如果Exchange是Fanout Exchange，这个参数会被忽略),
            // 第三个参数props是消息包含的属性信息。
            /* VERSION 1
            channel.basicPublish("", "firstQueue", null, message.getBytes());
            System.out.println("Send Message is '" + message + "'");
             */
            System.out.println("The send message's correlation id is:" + correlationId);
            channel.basicPublish("rpcSendExchange", "rpcSendMessage", props, number.getBytes());

            String response = null;

            while (true){
                // TODO
            }
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
