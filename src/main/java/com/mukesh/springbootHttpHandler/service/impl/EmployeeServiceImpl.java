package com.mukesh.springbootHttpHandler.service.impl;


import com.mukesh.springbootHttpHandler.dto.Employee;
import com.mukesh.springbootHttpHandler.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author Mukesh Bhoge
 **/
@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Override
    public Employee getEmployeeDetails() {
        return Employee.builder().employeeId(10)
                .firstName("Sachin").lastName("Tendulakar")
                .phoneNumber("1234567890").salary(1000000)
                .build();
        //return new Employee("Mukesh","B","12",12,1234567);
    }
}
