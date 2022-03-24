package com.mws.backend.account.service;

import com.mws.backend.account.interfaces.user.dto.UserCreateDto;
import com.mws.backend.account.interfaces.user.dto.UserUpdateDto;
import com.mws.backend.account.model.dao.UserDao;
import com.mws.backend.account.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public Long createUser(final UserCreateDto userCreateDto) {
        final User user = new User();
        user.setEmail(userCreateDto.getEmail());
        user.setPassword(userCreateDto.getPassword());
        user.setFirstName(userCreateDto.getFirstName());
        user.setLastName(userCreateDto.getLastName());

        User createdUser = userDao.create(user);
        return createdUser.getId();
    }

    public void updateUser(final UserUpdateDto userUpdateDto) {
        final User user = new User();
        user.setId(userUpdateDto.getId());
        user.setEmail(userUpdateDto.getEmail());
        user.setPassword(userUpdateDto.getPassword());
        user.setFirstName(userUpdateDto.getFirstName());
        user.setLastName(userUpdateDto.getLastName());

        userDao.update(user);
    }

}
