package com.xing.service.impl;

import com.xing.common.ApiException;
import com.xing.entity.User;

/**
 * @Class 	UserServiceI.java
 * @Author 	作者姓名:LiuXing
 * @Version	1.0
 * @Date	创建时间：2018-4-16 上午9:47:47
 * @Copyright Copyright by 智多星
 * @Direction 类说明
 */
public interface UserServiceI {
	
	Object Login( User user , String id) throws ApiException ;

}
