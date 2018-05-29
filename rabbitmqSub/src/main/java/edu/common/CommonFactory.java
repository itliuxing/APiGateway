package edu.common;

import com.rabbitmq.client.ConnectionFactory;

/**
* @Class 	CommonFactory.java
* @Author 	作者姓名:刘兴 
* @Version	1.0
* @Date		创建时间：2018年5月29日 下午20:42:52
* @Copyright Copyright by 刘兴
* @Direction 类说明
*/
public class CommonFactory {

    private static ConnectionFactory factory = null ;
    
    /***
     * 很简单的一个公共使用的方法
     * @return
     */
    public static ConnectionFactory getConnectionFactory() {
    	if( factory == null ) {
    		//连接方式
    		factory = new ConnectionFactory();
            factory.setHost( Constants.IP );
            factory.setUsername( Constants.USERNAME );
            factory.setPassword( Constants.PASSWORD );
            factory.setPort( Constants.PORT );
            factory.setVirtualHost( Constants.VHOST );
    	}
    	return factory ;
    }

}
