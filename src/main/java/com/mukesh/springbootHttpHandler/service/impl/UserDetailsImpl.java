package com.mukesh.springbootHttpHandler.service.impl;

import com.mukesh.springbootHttpHandler.adaptor.UserDetailsAdaptor;
import com.mukesh.springbootHttpHandler.dto.User;
import com.mukesh.springbootHttpHandler.service.IUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mukesh Bhoge
 **/
@Service
public class UserDetailsImpl implements IUserDetails {

    @Autowired
    private UserDetailsAdaptor userDetailsAdaptor;

    @Override
    public User getUserDetailById(Integer userId) {
//        return User.builder()
//                .id(1234)
//                .name("mukesh")
//                .email("mukesh@gmail.com")
//                .gender("male")
//                .status("Active")
//                .build();
        return userDetailsAdaptor.getUserById(userId);
    }
}
