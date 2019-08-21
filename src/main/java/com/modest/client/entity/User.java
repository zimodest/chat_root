package com.modest.client.entity;

import lombok.Data;

/**
 * description
 *
 * @author modest
 * @date 2019/08/18
 * @Description:实体类
 */

@Data
public class User {
    private Integer id;
    private String userName;
    private String password;
    private String brief;
}