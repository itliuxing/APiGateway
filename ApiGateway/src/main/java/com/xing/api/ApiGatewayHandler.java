package com.xing.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.xing.api.APiStore.ApiRunnable;
import com.xing.common.ApiException;
import com.xing.common.ApiHandlerUtil;
import com.xing.common.ApiRequestIn;
import com.xing.common.Result;

/**
* @Class 	ApiGatewaHandler.java
* @Author 	作者姓名:刘兴 
* @Version	1.0
* @Date		创建时间：2018年4月16日 上午10:56:13
* @Copyright Copyright by 刘兴
* @Direction 类说明		网关处理器
*/
@Component
public class ApiGatewayHandler implements InitializingBean , ApplicationContextAware {
	
	private static final Logger log = Logger.getLogger( ApiGatewayHandler.class ) ;
	
	private static final String METHOD = "method" ;
	private static final String PARAMS = "params" ;
	
	APiStore apiStore ;
	final ParameterNameDiscoverer parameteUtil ;
	
	public ApiGatewayHandler() {
		parameteUtil = new LocalVariableTableParameterNameDiscoverer() ;
	}
	

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		apiStore = new APiStore( context ) ;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		apiStore.loadApiFormspringBeans();
	}

	/****
	 * 请求的真正处理器
	 * @param request
	 * @param response
	 */
	public void handler( HttpServletRequest request, HttpServletResponse response ) {
		/* 老处理方式
		String params = request.getParameter( PARAMS ) ;
		String method = request.getParameter( METHOD ) ;*/
		//通用处理方式 --- 将数据处理封装 //
		ApiRequestIn requestIn = ApiHandlerUtil.getRead2Obj(request) ;
		String params = requestIn.getParams() ;
		String method = requestIn.getMethod() ;
		
		Object result ;
		
		ApiRunnable apiRun  = null ;
		try {
			apiRun = sysParamsValdate( requestIn ) ;
			log.info( "请求接口={" + method + "} 参数=" + params  );
			Object[] args = buildParams( apiRun, params, request, response) ;
			result = apiRun.run(args) ;
		}catch (ApiException e) {
			response.setStatus( 500 );
			log.error( "请求接口={" + method + "}异常 参数=" + params );
			result = handlerError( e ) ;
		}catch (InvocationTargetException e) {
			response.setStatus( 500 );
			log.error( "请求接口={" + method + "}异常 参数=" + params , e.getTargetException() );
			result = handlerError( e ) ;
		}catch (Exception e) {
			response.setStatus( 500 );
			log.error( "其他异常：" , e );
			result = handlerError( e ) ;
		}
		//统一返回结果
		returnResult(result, response);
	}
	
	private Object handlerError( Throwable throwable ) {
		String code = "" ;
		String message = "" ;
		if( throwable instanceof ApiException ) {
			code = "0001" ;
			message = throwable.getMessage() ;
		}
		//扩展异常规范
		else {
			code = "0002" ;
			message = throwable.getMessage() ;
		}
		Result result= new Result() ;
		result.setCode(code);
		result.setMessage(message);
		return result ;
	}
	
	
	private Object[] buildParams( ApiRunnable apiRun , String paramsJson , HttpServletRequest request, 
			HttpServletResponse response ) throws ApiException {
		Map<String , Object> map = null ;
		try {
			//ObjectMapper mapper = new ObjectMapper(); 			//转换器
			//map = mapper.readValue( paramsJson , Map.class ) ;	//json 转成map
			map = JSON.parseObject( paramsJson, Map.class );
		}catch (Exception e) {
			throw new ApiException("调用失败：json 字符串格式异常，请检查params参数 ") ;
		}
		if( map == null) {
			map = new HashMap<>() ;
		}
		
		Method method = apiRun.getTargetMethod() ;
		List<String> paramNames = Arrays.asList( parameteUtil.getParameterNames(method) ) ;
		//获取接口的参数列表
		
		Class<?>[] paramtype = method.getParameterTypes() ;
		
		for( Map.Entry<String, Object> m : map.entrySet() ) {
			if(! paramNames.contains( m.getKey() ) ) {
				throw new ApiException( "接口调用失败：接口不存在 " + m.getKey() + " 参数.") ;
			}
		}
		
		Object[] args = new Object[ paramtype.length ] ;
		for( int i=0;i<paramtype.length;i++ ) {
			if( paramtype[i].isAssignableFrom( HttpServletRequest.class ) ) {
				args[i] = request ;
			}else if ( map.containsKey(paramNames.get(i) )) {
				try {
					//将请求参数据，转换成指定得传入得类型参数
					args[i] = convertJsonToBean( map.get( paramNames.get(i) ), paramtype[i] ) ;					
				}catch (Exception e) {
					throw new ApiException("调用失败：指定参数据格式错误或者值错误：" + paramNames.get(i)
					+ e.getMessage() )  ;
				}
			}else {
				args[i] = null ;
			}
		}
		return args ;
	}
	
	private <T> Object convertJsonToBean( Object val ,Class<T> tergatClass ) throws Exception {
		Object result = null ;
		if( val == null) {
			return null ;
		}else if( Integer.class.equals(tergatClass) ) {
			result = Integer.parseInt( val.toString() ) ;
		}else if( Long.class.equals(tergatClass) ) {
			result = Long.parseLong( val.toString() ) ;
		}else if( Date.class.equals(tergatClass) ) {
			if( val.toString().matches("[0-9]+") ) {
				result = new Date( Long.parseLong(val.toString()) ) ;
			}else {
				throw new IllegalArgumentException("日期必须是长整形得时间戳") ;
			}
		}else if( String.class.equals(tergatClass) ) {
			if( val instanceof String ) {
				result = val ;
			}else {
				throw new IllegalArgumentException("转换目标类型为字符串.") ;
			}
		}else {
			//这里可能会有问题
			result = JSON.parseObject( val.toString() , tergatClass ) ;
		}
		return result ;
	}
	
	public void returnResult( Object result ,HttpServletResponse response ) {
		try {

			String json = JSON.toJSONString(result) ;	//返回值转换成JSON
			
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html/json;charset=utf-8");
			response.setHeader( "Cache-Control" , "no-cache" );
			response.setHeader( "Pragma" ,"no-cache" );
			response.setDateHeader( "Expires",0 );
			if( json != null ) {
				response.getWriter().write(json);
			}
		}catch (Exception e) {
			log.error("服务中心响应异常....");
			throw new RuntimeException( e ) ;
		}
	}
	
	/***
	 * 根据method获取到缓存=得service 信息
	 * @return
	 */
	public ApiRunnable sysParamsValdate( ApiRequestIn requestIn ) {
		/*String apiName = request.getParameter(METHOD) ;
		String json = request.getParameter(PARAMS) ;*/
		String json = requestIn.getParams() ;
		String apiName = requestIn.getMethod() ;
		
		ApiRunnable apiRun = null ;
		
		if( StringUtils.isEmpty( apiName ) ) {
			throw new ApiException("调用失败：缺少参数'method'") ;
		}else if( StringUtils.isEmpty( json ) ) {
			throw new ApiException("调用失败：缺少参数'params'") ;
		}else if( (apiRun = apiStore.findApiRunnableByApiName(apiName)) == null ) {
			throw new ApiException("调用失败：指定API不存在，API：" + apiName ) ;
		}
		return apiRun ;
	}
}
