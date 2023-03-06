package com.example.onetoone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@SpringBootApplication
public class OnetooneApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnetooneApplication.class, args);
	}

	@Bean
	ApplicationListener <ApplicationReadyEvent> readyEventApplicationListener (EmployeeService es){
		return event -> es.all().forEach(System.out::println);
	}
}

@Service
class EmployeeService{
	private final JdbcTemplate template ;

	public final RowMapper<Employee> employeeRowMapper =
			(rs, rowNum) -> new Employee( rs.getInt("id"), rs.getString("name"));

	EmployeeService(JdbcTemplate template){
		this. template =template;
	}

	Employee byName(String name) {
		return this.template.queryForObject("select * from employees where name =?", this.employeeRowMapper, name);
	}

	Collection <Employee> all (){
		return this.template.query("select * from employees", this.employeeRowMapper);
	}
}

record Employee (Integer id, String name){}