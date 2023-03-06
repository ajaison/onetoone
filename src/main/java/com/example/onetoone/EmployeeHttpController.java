package com.example.onetoone;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

@Controller
@ResponseBody
public class EmployeeHttpController {
    private final EmployeeService service;

    EmployeeHttpController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping("/employees")
    Collection<Employee> all() {
        return this.service.all();
    }

    @GetMapping("/employees/{name}")
    public ResponseEntity<Employee> byName(@PathVariable("name") String name) {
        Employee employee = this.service.byName(name);
        if (employee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }
}
