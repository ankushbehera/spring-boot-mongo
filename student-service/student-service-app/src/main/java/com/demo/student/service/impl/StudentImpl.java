package com.demo.student.service.impl;


import com.demo.student.model.Student;
import com.demo.student.repository.StudentRepository;
import com.demo.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ANkush
 * @since 06-06-2021
 */

@Service
public class StudentImpl implements StudentService {

    @Autowired
    private StudentRepository repository;

    @Override
    public List<Student> getAllStudents() {
        return repository.findAll();
    }
}