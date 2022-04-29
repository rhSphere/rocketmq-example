package com.prestigeding.user.mapper;

import com.prestigeding.user.model.UserLoginLogger;
import tk.mybatis.mapper.common.Mapper;

public interface UserLoginLoggerMapper extends Mapper<UserLoginLogger> {

    //   void insert(UserLoginLogger userLoginLogger);

       int count(Long userId,long start,long end);

}
