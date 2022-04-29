package com.prestigeding.user.service;

import java.util.Map;

/**
 * dubbo 服务
 */
public interface UserService {


    /**
     * 登录
     *
     * @param userName
     * @param password
     * @return
     */
    Map<String, Object> login(String userName, String password);


}
