package com.xing.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.xing.api.APIMapping;
import com.xing.common.ApiException;
import com.xing.entity.User;

/**
 * @Class 	UserServiceImpl.java
 * @Author 	作者姓名:LiuXing
 * @Version	1.0
 * @Date	创建时间：2018-4-16 上午9:49:53
 * @Copyright Copyright by 智多星
 * @Direction 类说明
 */
@Service("userService")
public class UserServiceImpl implements UserServiceI {
	
	@APIMapping("user.login")
	public Object Login(User user , String id ) throws ApiException {
		if( StringUtils.isEmpty(user) ||  StringUtils.isEmpty(user.getName()) ||
				StringUtils.isEmpty(user.getName() ) ||  StringUtils.isEmpty( id ) ){
			throw new ApiException( "登陆失败，账户与密码不符." ) ;
		}
		user.setPass("哈哈哈，我收到你的数据了.");
		return user;
	}

}
