package com.mws.back_end.account.service.security;

import java.util.Collection;
import java.util.Optional;

import com.mws.back_end.account.interfaces.user.dto.UserDto;
import com.mws.back_end.account.model.dao.UserDao;
import com.mws.back_end.account.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mws.back_end.account.interfaces.user.dto.UserDto.toDto;
import static com.mws.back_end.framework.utils.StringUtils.isEmpty;
import static java.util.Collections.singletonList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String loginEmail) {
        User user = userDao.findByEmail(loginEmail);
        if (user == null) {
            throw new UsernameNotFoundException("No user found with login email: " + loginEmail);
        }

        return new org.springframework.security
                .core.userdetails.User(user.getEmail(), user.getPassword(),
                true, true, true,
                true, getUserAuthorities());
    }

    private Collection<? extends GrantedAuthority> getUserAuthorities() {
        return singletonList(new SimpleGrantedAuthority("USER"));
    }

    public Optional<UserDto> getUserByEmail(final String email) {
        if (isEmpty(email)) {
            return Optional.empty();
        }

        final User userOpt = userDao.findByEmail(email);
        if (userOpt != null) {
            return Optional.of(toDto(userOpt));
        }

        return Optional.empty();
    }
}
