package com.xing.common;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.xing.api.APiStore;

/***
 * *
 * 类名称：		SystemServerListener.java 
 * 类描述：   	系统自动启动后spring bean管理器到缓存
 * 创建人：		
 * 创建时间：		2017-5-9下午8:06:13 
 * 修改人：		liuxing
 * 修改时间：		2017-5-9下午8:06:13 
 * 修改备注：   		
 * @version
 */
public class SystemServerListener implements ServletContextListener{
	
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent context) {
		APiStore.applicationContextInfo = WebApplicationContextUtils.getRequiredWebApplicationContext(context.getServletContext());
	}
	
	/****
	 * 延迟加载信息业务，不影响主进程的运作
	 */
	public void delayLoad(){
	}

}
