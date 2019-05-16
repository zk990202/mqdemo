package again._5topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class TopicSend {
    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception {
        Connection connection = null;
        Channel channel = null;

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("192.168.150.129");
            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");

            String[] routingKeys = new String[]{"quick.orange.rabbit", "lazy.orange.elephant", "quick.orange.fox",
                "lazy.brown.fox", "quick.brown.fox", "quick.orange.male.rabbit.", "lazy.orange.male.rabbit"};

            for (String severity : routingKeys) {
                String message = "From " + severity + " routingKey's message!";
                channel.basicPublish(EXCHANGE_NAME, severity, null, message.getBytes());
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
