package com.xing.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;


/**
 * @Class 	ApiGatewayServlet.java
 * @Author 	作者姓名:LiuXing
 * @Version	1.0
 * @Date	创建时间：2018-4-16 上午9:41:59
 * @Copyright Copyright by 智多星
 * @Direction 类说明	网关的入口
 */
public class ApiGatewayServlet extends HttpServlet  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationContext context ;
	private ApiGatewayHandler APiHandler ;
	
	
	
	/***
	 * 拥spring 管理网关
	 */
	public void init() throws ServletException {
		super.init(); 
		context = APiStore.applicationContextInfo ;
		APiHandler = context.getBean( ApiGatewayHandler.class ) ;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		APiHandler.handler(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		APiHandler.handler(request, response);
	}

}
