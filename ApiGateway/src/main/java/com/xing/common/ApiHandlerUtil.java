package com.xing.common;

import java.io.BufferedReader;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

/**
* @Class 	ApiHandlerUtil.java
* @Author 	作者姓名:刘兴 
* @Version	1.0
* @Date		创建时间：2018年4月17日 上午10:49:26
* @Copyright Copyright by 刘兴
* @Direction 类说明
*/
public class ApiHandlerUtil {
	
	private final static Logger logger = Logger.getLogger( ApiHandlerUtil.class ) ;
	

	private static final String METHOD = "method" ;
	private static final String PARAMS = "params" ;
	
	/***
	 * 读取请求数据信息
	 * @param request
	 * @return
	 */
	private static String readJSONString(HttpServletRequest request){
		StringBuffer json = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while((line = reader.readLine()) != null) {
				json.append(line);
			}
		}catch(Exception e) {
			System.out.println(e.toString());
		}
		return json.toString();
	}
	
	/****
	 * 将请求数据封装处理，返回成请求对象
	 * @param request
	 * @return
	 */
	public  static ApiRequestIn getRead2Obj(HttpServletRequest request){
		ApiRequestIn requestIn = new ApiRequestIn();
		String requestMethod = request.getMethod();
		String params = null, method = null;
		if ("GET".equalsIgnoreCase(requestMethod)) {

			params = request.getParameter(PARAMS);
			method = request.getParameter(METHOD);

		} else if ("POST".equalsIgnoreCase(requestMethod)) {
			String paramsJson = readJSONString(request);
			Map<String, Object> map = null;
			try {
				map = JSON.parseObject(paramsJson, Map.class);
			} catch (Exception e) {
				logger.error("==================参数请求格式异常........,遭受攻击.............====================");
			}
			try {
				params = map.get(PARAMS).toString();
			} catch (Exception e) {
			}
			try {
				method = map.get(METHOD).toString();
			} catch (Exception e) {
			}
		}

		requestIn.setMethod(method);
		requestIn.setParams(params);
		return requestIn;
	}

}
