package com.demo.student.utils;

import com.demo.student.model.Student;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;


/**
 * @author ANkush
 * @since 06-06-2021
 */
public class HelperUtil {

    private HelperUtil() {
    }


    public static Supplier<List<Student>> studentDataSupplier = () ->
            Arrays.asList(
                    Student.builder().name("Student1").build(),
                    Student.builder().name("Student2").build(),
                    Student.builder().name("Student3").build()
            );
}
