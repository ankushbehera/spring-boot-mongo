package com.demo.student.repository;

import com.demo.student.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author ANkush
 * @since 06-06-2021
 */
public interface StudentRepository extends MongoRepository<Student, Integer> {

}
