package com.example.demo.entity;

import lombok.Data;
import java.io.Serializable;

/**
 * @Author: yuanpusheng
 * @Date: 2019/3/24 14:36
 * @Version 1.0
 */

@Data
//lombok依赖，可省略get set方法
public class User implements Serializable {

    private int userId;

    private String userName;

    private String userPassword;

    public User(int userId, String userName, String userPassword) {
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;
    }
}

