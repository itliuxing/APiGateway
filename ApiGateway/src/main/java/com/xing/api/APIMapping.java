package com.xing.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Class 	APIMapping.java
 * @Author 	作者姓名:LiuXing
 * @Version	1.0
 * @Date	创建时间：2018-4-16 上午9:27:06
 * @Copyright Copyright by 智多星
 * @Direction 类说明
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface APIMapping {
	
	String value() ;
	
	//登陆检测
	boolean checkLogin()  default false ;
	

}
