package com.lx.server.service;

import com.lx.server.pojo.UserClient;

/**
 * 【钱包客户端用户】 服务类 接口
 *
 * @author AutoCode 309444359@qq.com
 * @date 2019-04-13 23:59:45
 *
 */
public interface UserClientService extends MybatisBaseService {

	UserClient createNewUser(String userId,String nickname);

}
