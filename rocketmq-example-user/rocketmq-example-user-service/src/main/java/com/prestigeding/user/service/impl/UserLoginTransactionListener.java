package com.prestigeding.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.prestigeding.user.mapper.UserLoginLoggerMapper;
import com.prestigeding.user.mapper.UserLoginTransactionMapper;
import com.prestigeding.user.model.UserLoginLogger;
import com.prestigeding.user.model.UserLoginTransaction;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class UserLoginTransactionListener implements TransactionListener {

    @Autowired
    private UserLoginLoggerMapper userLoginLoggerMapper;

    @Autowired
    private UserLoginTransactionMapper userLoginTransactionMapper;


    @Override
    @Transactional
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {

        //　核心思路，登录日志(业务数据)　与本地事务状态表在同一个事务中
        UserLoginLogger loginLogger = JSON.parseObject(msg.getBody(), UserLoginLogger.class);
        userLoginLoggerMapper.insert(loginLogger);

        UserLoginTransaction userLoginTransaction = new UserLoginTransaction();
        userLoginTransaction.setSerialNumber(loginLogger.getSerialNumber());
        userLoginTransaction.setCreateTime(new Date(System.currentTimeMillis()));
        userLoginTransactionMapper.insert(userLoginTransaction);

        //模拟异常,判断事务会不会成功
        int a = 1;
        if (a == 1) {
            throw new RuntimeException();
        }

        return LocalTransactionState.UNKNOW;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {

        UserLoginLogger loginLogger = JSON.parseObject(msg.getBody(), UserLoginLogger.class);

        UserLoginTransaction queryModel = new UserLoginTransaction();
        queryModel.setSerialNumber(loginLogger.getSerialNumber());

        UserLoginTransaction transaction = userLoginTransactionMapper.selectOne(queryModel);

        if (transaction != null) { // 如果能查询到，则提交消息
            return LocalTransactionState.COMMIT_MESSAGE;
        }


        // 如果事务状态表中未查询到，返回事务状态未知，RocketMQ默认在15次回查后将该消息回滚
        return LocalTransactionState.UNKNOW;
    }
}
