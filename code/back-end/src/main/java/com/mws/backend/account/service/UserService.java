package com.mws.backend.account.service;

import com.mws.backend.account.model.dao.UserDao;
import com.mws.backend.account.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    //@Qualifier("UserDaoImpl")
    private UserDao userDao;


    public Long createUser(final String email) {
        final User user = new User();
        user.setEmail(email);

        final List<String> violatedConstraints = validate(user);
        if (violatedConstraints.isEmpty()) {
            User createdUser = userDao.create(user);
            return createdUser.getId();
        }

        throw new RuntimeException("violatedConstraints: " + violatedConstraints.size());
    }

    private List<String> validate(User user) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<User>> violatedConstraints = validator.validate(user);
        return violatedConstraints.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
    }

    public User getUser(final String email) {
        return null;//userDao.findByEmail(email);
    }
}
