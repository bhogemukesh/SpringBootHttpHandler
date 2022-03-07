package com.mukesh.springbootHttpHandler.web;

import com.mukesh.springbootHttpHandler.dto.User;
import com.mukesh.springbootHttpHandler.service.IUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.List;

/**
 * @author Mukesh Bhoge
 **/
@RestController
public class UserDetailsController {

    @Autowired
    private IUserDetails userDetails;

    @GetMapping(value = "/getUserDetails/{userId}",produces = "application/json")
    @ResponseBody
    public User getUserDetails(@Validated @PathVariable final String userId){
        System.out.println("userId="+userId);
        return userDetails.getUserDetailById(Integer.parseInt(userId));
    }

}
