package com.mws.backend.account.model.dao;

import com.mws.backend.account.model.entity.User;
import com.mws.backend.framework.database.GenericDaoImpl;
import org.springframework.stereotype.Component;

@Component
public class UserDao extends GenericDaoImpl<User, Long>  {

}

