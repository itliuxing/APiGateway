package com.xing.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.support.ControllerClassNameHandlerMapping;

/**
* @Class 	APiStore.java
* @Author 	作者姓名:刘兴 
* @Version	1.0
* @Date		创建时间：2018年4月16日 上午11:06:32
* @Copyright Copyright by 刘兴
* @Direction 类说明
*/
public class APiStore {
	

	public static ApplicationContext applicationContextInfo;
	private ApplicationContext applicationContext ;
	
	//接口识别后缓存此处
	private HashMap<String , ApiRunnable> apiMap = new HashMap<>() ;
	
	public APiStore( ApplicationContext applicationContext ) {
		Assert.notNull( applicationContext );
		this.applicationContext = applicationContext ;
	}
	
	public void loadApiFormspringBeans( ) {
		// ioc 所有的bean
		//spring ioc 扫描
		String[] names = applicationContext.getBeanDefinitionNames() ;
		Class<?> type ;
		//反射处理
		for( String name : names ) {
			type = applicationContext.getType(name) ;
			for( Method m : type.getDeclaredMethods() ) {
				//通过反射难道APImapping 注解
				APIMapping apiMapping = m.getAnnotation( APIMapping.class ) ;
				if( apiMapping != null ) {
					addApiitem(apiMapping, name , m );
				}
			}
		}
		
	}
	
	private void addApiitem( APIMapping apiMapping , String beanName ,Method method) {
		ApiRunnable apiRun = new ApiRunnable() ;
		apiRun.apiName = apiMapping.value() ;
		apiRun.targetMethod = method ;
		apiRun.targetName = beanName ;
		apiMap.put( apiMapping.value() , apiRun ) ;
	}
	
	public ApiRunnable findApiRunnable( String apiName,String  version ){
			return (ApiRunnable)apiMap.get(apiName + "_" + version) ;
	}
	
	/***
	 * 缓存中是含有此接口的信息
	 * @param apiName
	 * @return
	 */
	public List<ApiRunnable> findApiRunnable( String apiName ){
		if( apiName == null ) {
			throw new IllegalArgumentException("api name must not null") ;
		}
		List<ApiRunnable> list = new ArrayList<>(20) ;
		for( ApiRunnable api : apiMap.values() ) {
			if( api.getApiname().equals(apiName)) {
				list.add(api) ;
			}
		}
		return list ;
	}
	
	/***
	 * 缓存中是含有此接口的信息
	 * @param apiName
	 * @return
	 */
	public ApiRunnable findApiRunnableByApiName( String apiName ){
		if( apiName == null ) {
			throw new IllegalArgumentException("api name must not null") ;
		}
		ApiRunnable apiRunnable = null ;
		for( ApiRunnable api : apiMap.values() ) {
			if( api.getApiname().equals(apiName)) {
				apiRunnable = api ;
				break ;
			}
		}
		return apiRunnable ;
	}
	
	public List<ApiRunnable> getAll(){
		 List<ApiRunnable> list = new ArrayList<>( 20 ) ;
		 list.addAll( apiMap.values() ) ;
		 Collections.sort(list,new Comparator<ApiRunnable>() {
	            @Override
	            public int compare(ApiRunnable o1, ApiRunnable o2) {
	                // 返回值为int类型，大于0表示正序，小于0表示逆序
	                return o1.getApiname().compareTo(o2.getApiname());
	            }
	        });	
		return list ;
	}
	
	public boolean containsApi(String apiName ,String version) {
		return apiMap.containsKey(apiName + "_" + version) ;
	}
	
	public ApplicationContext getApplicationContext() { return applicationContext ; }
	
	public class ApiRunnable {
		
		String apiName ;			//接口的注解名称    user.login
		
		String targetName ;			//ioc bean名称
		
		Object target ;				//目标接口实例
		
		Method targetMethod ;		//调用接口的目标方法
		
		//多线程处理安全
		public Object run( Object... args  ) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			if( target == null ) {
				target = applicationContext.getBean( targetName ) ;
			}
			return targetMethod.invoke(target, args) ;
		}
		
		public Class<?>[] getParamTypes(){ return targetMethod.getParameterTypes() ; }
		
		public String getApiname(){ return apiName ; }
		
		public String getTargetName() { return targetName ; }
		
		public Object getTarget() { return target ; }
		
		public Method getTargetMethod() { return targetMethod ; }

	}

}

