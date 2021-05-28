package com.demo.student.model;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ANkush
 * @since 06-06-2021
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "student")
public class Student implements Serializable {

    @Id
    private String id;
    String name;
}