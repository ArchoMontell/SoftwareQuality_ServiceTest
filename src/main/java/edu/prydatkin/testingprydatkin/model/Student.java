package edu.prydatkin.testingprydatkin.model;


/*
    @author lilbl
    @project testingPrydatkin
    @class Student
    @version 1.0.0
    @since 5/3/2025 - 12.38
*/

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Document
public class Student {

    @Id
    private String id;
    private String name;
    private int age;
    private String gender;

    private LocalDateTime createDate;
    private List<LocalDateTime> updateDate;

    public Student(String name, int age, String gender) {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public Student(String id, String name, int age, String gender) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Student student)) return false;

        return getId().equals(student.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
