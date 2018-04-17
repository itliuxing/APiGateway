package com.xing.common;

import java.io.Serializable;

/**
* @Class 	ApiRequestInParams.java
* @Author 	作者姓名:刘兴 
* @Version	1.0
* @Date		创建时间：2018年4月17日 上午10:56:14
* @Copyright Copyright by 刘兴
* @Direction 类说明
*/
public class ApiRequestIn implements Serializable {
	
	private String method ;
	private String params ;
	
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	
	

}
