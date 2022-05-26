package com.mws.back_end.account.service;

import com.mws.back_end.account.interfaces.user.dto.UserCreationDto;
import com.mws.back_end.account.interfaces.user.dto.UserUpdateDto;
import com.mws.back_end.account.model.dao.UserDao;
import com.mws.back_end.account.model.entity.User;
import com.mws.back_end.framework.exception.EntityPersistenceException;
import com.mws.back_end.framework.exception.MWSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public Long createUser(final UserCreationDto userCreationDto) throws MWSException {
        try {
            userDao.find(1L);
            return userDao.create(toUser(userCreationDto)).getId();
        } catch (EntityPersistenceException e) {
            throw new MWSException(e.getMessage());
        }
    }

    private User toUser(UserCreationDto userCreationDto) {
        final User user = new User();
        user.setEmail(userCreationDto.getEmail());
        user.setPassword(userCreationDto.getPassword());
        user.setFirstName(userCreationDto.getFirstName());
        user.setLastName(userCreationDto.getLastName());

        return user;
    }

    public void updateUser(final UserUpdateDto userUpdateDto) throws MWSException {
        final User user = toUser(userUpdateDto);

        if (userDao.findWeak(user.getId()) == null) {
            throw new MWSException("Entity not found");
        }

        if (!userDao.findByEmail(userUpdateDto.getEmail()).isEmpty()) {
            throw new MWSException("Duplicated email");
        }

        try {
            userDao.update(user);
        } catch (EntityPersistenceException e) {
            throw new MWSException(e.getMessage());
        }
    }

    private User toUser(UserUpdateDto userUpdateDto) {
        final User user = new User();
        user.setId(userUpdateDto.getId());
        user.setEmail(userUpdateDto.getEmail());
        user.setPassword(userUpdateDto.getPassword());
        user.setFirstName(userUpdateDto.getFirstName());
        user.setLastName(userUpdateDto.getLastName());

        return user;
    }

}
