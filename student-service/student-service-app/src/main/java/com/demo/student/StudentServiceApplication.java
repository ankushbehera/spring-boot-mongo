package com.demo.student;

import com.demo.student.model.Student;
import com.demo.student.repository.StudentRepository;
import com.demo.student.utils.HelperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;


@SpringBootApplication
@EnableMongoRepositories
public class StudentServiceApplication {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	public static void main(String[] args) {
		SpringApplication.run(StudentServiceApplication.class, args);
	}

	@Autowired
	private StudentRepository studentRepository;

	
	@Bean
	CommandLineRunner runner() {
		return args -> {
			List<Student> students = studentRepository.findAll();
			if (students.size() == 0) {
				LOGGER.info("Inserting Student data to DB");
				studentRepository.saveAll(HelperUtil.studentDataSupplier.get());
			} else {
				LOGGER.info("Student data stored in total:: {} and Data :: {}",students.size(), students);
			}
		};
	}

}