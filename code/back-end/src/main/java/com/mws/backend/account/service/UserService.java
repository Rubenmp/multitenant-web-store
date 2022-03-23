package com.mws.backend.account.service;

import com.mws.backend.account.model.dao.UserDao;
import com.mws.backend.account.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;


    public User createUser(final String email) {
        User user = new User();
        user.setEmail(email);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<User>> violatedConstraints = validator.validate(user);

        if (violatedConstraints.isEmpty()) {
            return userDao.save(user);
        }
        throw new RuntimeException("violatedConstraints: " + violatedConstraints.size());
    }

    public User getUser(final String email) {
        return userDao.findByEmail(email);
    }
}
