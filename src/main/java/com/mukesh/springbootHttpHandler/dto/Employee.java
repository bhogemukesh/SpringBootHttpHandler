package com.mukesh.springbootHttpHandler.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author Mukesh Bhoge
 **/
@Data
@Builder
public class Employee {
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final Integer employeeId;
    private final Integer salary;
}
