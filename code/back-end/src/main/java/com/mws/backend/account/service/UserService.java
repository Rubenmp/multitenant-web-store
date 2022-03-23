package com.mws.backend.account.service;

import com.mws.backend.account.model.dao.UserDao;
import com.mws.backend.account.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;


    public User createUser(final String email) {
        User user = new User();
        user.setEmail(email);

        return userDao.save(user);
    }

    public User getUser(final String email) {
        return userDao.findByEmail(email);
    }
}
