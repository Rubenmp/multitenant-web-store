package com.mws.backend.account.service;

import com.mws.backend.account.interfaces.user.dto.UserCreationDto;
import com.mws.backend.account.interfaces.user.dto.UserUpdateDto;
import com.mws.backend.account.model.dao.UserDao;
import com.mws.backend.account.model.entity.User;
import com.mws.backend.framework.exception.EntityPersistenceException;
import com.mws.backend.framework.exception.MWSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public Long createUser(final UserCreationDto userCreationDto) throws MWSException {
        final User user = new User();
        user.setEmail(userCreationDto.getEmail());
        user.setPassword(userCreationDto.getPassword());
        user.setFirstName(userCreationDto.getFirstName());
        user.setLastName(userCreationDto.getLastName());

        try {
            return userDao.create(user).getId();
        } catch (EntityPersistenceException e) {
            throw new MWSException(e.getMessage());
        }
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
