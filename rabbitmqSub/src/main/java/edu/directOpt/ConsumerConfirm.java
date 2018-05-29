package edu.directOpt;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel ;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import edu.common.CommonFactory;
import edu.common.Constants;

/**
 * 
 * @Class 	ConsumerConfirm.java
 * @Author 	作者姓名:Liuxing
 * @Version	1.0
 * @Date	创建时间：2018年5月29日 下午20:41:17
 * @Copyright Copyright by Liuxing
 * @Direction 类说明	消费者，获取-----RabbitMQ，vhost 下面的 EXCHANGE交换器下面的QUEUE
 */
public class ConsumerConfirm {

    public static void main(String[] args) throws IOException, TimeoutException,
            InterruptedException {
    	
    	//我再封装了一层，没办法 导出都要用，但是工厂其实只需要创建一份就好了
    	ConnectionFactory factory = CommonFactory.getConnectionFactory() ;

        Connection connection = factory.newConnection();	//创建连接
        
        Channel channel = connection.createChannel();		//连接创建信道
        
        //交换器 四种交换器，direct、fanout、topic、handles			//绑定哪个交换器,理论上用命令去建
        channel.exchangeDeclare( Constants.EXCHANGE_NAME , BuiltinExchangeType.DIRECT );
        //创建队列（就是所谓的路由键）消费者就只需要直接去取就可以了
        //channel.queueDeclare( CommonFactory.QUEUE_NAME, false, false, false, null);	
        channel.queueBind( Constants.QUEUE_NAME ,Constants.EXCHANGE_NAME ,Constants.RoutingKey  );
        
        System.out.println("Waiting message.......");

        Consumer consumerB = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                AMQP.BasicProperties properties, byte[] body) throws IOException {
            	
            	//消费消息出现异常时，我们需要取消确认，这时我们可以使用 Channel 的 basicReject 方法（不过这数据还是会被下一个消费者拿到，消费可能会死循环）
                //this.getChannel().basicReject(envelope.getDeliveryTag(),true);
                
                //确认一条消息已经被消费，我们调用的 basicAck 方法的第一个参数是 Delivery Tag
                this.getChannel().basicAck(envelope.getDeliveryTag(),false);
                
                //假如忘记确认消息被消费了怎么办，呵呵呵 在当前连接未断的情况下，会一直处于未确认消费的状态，一旦连接失效则再次放回待消费队列中
                //this.getChannel().basicAck(envelope.getDeliveryTag(),false);
                
                //这个是那个路由键转发过来的------->可以不同的路由键转发到同一个队列
                String routingKey = envelope.getRoutingKey() ;
                //打印取到的队列的消息，所以说理论上来讲，没有强事务，理论上队列处理也是没有问题的.
                System.out.println( "接收到消息："+new String(body,"UTF-8"));
            }
        };

        channel.basicConsume( Constants.QUEUE_NAME ,false,consumerB );

    }

}
