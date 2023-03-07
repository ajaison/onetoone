package com.example.onetoone;

import jakarta.annotation.PreDestroy;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@Controller
@ResponseBody
public class EmployeeHttpController {
    private final EmployeeService service;

    EmployeeHttpController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping("/employees")
    public ResponseEntity<Collection<Employee>> all() {
        Collection<Employee> employees = this.service.getAllEmployees();
        if (employees == null || employees.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @GetMapping("/employees/{name}")
    public ResponseEntity<Employee> byName(@PathVariable("name") String name) {
        try {
            Assert.state(Character.isUpperCase(name.charAt(0)), "The name must start with a capital letter");
            Employee employee = this.service.byName(name);
            if (employee == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(employee, HttpStatus.OK);
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while processing the request", ex);
        }
    }
}

@ControllerAdvice
class ErrorHandlingControllerAdvice {
    @ExceptionHandler
    ProblemDetail handleIllegalStateException(IllegalStateException exception) {
        var pd = ProblemDetail.forStatus(HttpStatusCode.valueOf(500));
        pd.setDetail("First letter needs to be caps");
        return pd;
    }
}
