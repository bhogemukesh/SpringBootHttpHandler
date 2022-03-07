package com.mukesh.springbootHttpHandler.service;

import com.mukesh.springbootHttpHandler.dto.User;

/**
 * @author Mukesh Bhoge
 **/
public interface IUserDetails {

    public User getUserDetailById(Integer userId);
}
