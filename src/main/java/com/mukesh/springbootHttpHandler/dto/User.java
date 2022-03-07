package com.mukesh.springbootHttpHandler.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author Mukesh Bhoge
 **/
@Data
public class User {
    private int id;
    private String name;
    private String email;
    private String gender;
    private String status;
}
