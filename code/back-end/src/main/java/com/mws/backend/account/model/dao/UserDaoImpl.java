package com.mws.backend.account.model.dao;

import com.mws.backend.account.model.entity.User;
import com.mws.backend.framework.database.GenericDao;
import com.mws.backend.framework.database.GenericDaoImpl;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class UserDaoImpl extends GenericDaoImpl<User, Long> implements UserDao {

}

