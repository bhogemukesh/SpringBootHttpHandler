package com.mukesh.springbootHttpHandler.adaptor;

import com.mukesh.springbootHttpHandler.dto.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;

/**
 * @author Mukesh Bhoge
 **/
@Component
@Slf4j
public class UserDetailsAdaptor {

    @Autowired
    @Qualifier("userHttpsHandler")
    private HttpHandler userHttpsHandler;



    private String url = "https://gorest.co.in/public/v2/users/{userId}";

    public User getUserById(Integer userId){
        HttpEntity<User> entity =  new HttpEntity<>(new HttpHeaders()) ;
        User user = userHttpsHandler.callRestTemplate("USER_DETAILS",url, HttpMethod.GET,entity,User.class, Collections.singletonMap("userId",userId), HttpStatus.OK);
        return user;
    }

}
