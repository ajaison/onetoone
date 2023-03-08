package com.example.onetoone;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.function.Supplier;

@Controller
@ResponseBody
public class EmployeeHttpController {
    private final EmployeeService service;
    private final ObservationRegistry registry;

    EmployeeHttpController(EmployeeService service, ObservationRegistry registry) {
        this.service = service;
        this.registry = registry;
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
    public ResponseEntity<Employee> byName(@PathVariable("name") String name) throws IllegalStateException {
        Assert.state(Character.isUpperCase(name.charAt(0)), "The name must start with a capital letter");

        // Create an observation for the byName method
        Observation observation = Observation.createNotStarted("byName", this.registry);

        // Use the observe method to wrap the service call
        Employee employee = observation.observe(() -> {
            return this.service.byName(name);
        });

        if (employee == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(employee, HttpStatus.OK);
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
