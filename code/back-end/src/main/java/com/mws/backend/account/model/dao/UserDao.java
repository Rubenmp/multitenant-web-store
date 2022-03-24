package com.mws.backend.account.model.dao;

import com.mws.backend.account.model.entity.User;
import com.mws.backend.framework.database.GenericDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

public interface UserDao extends GenericDao<User, Long> {

}

