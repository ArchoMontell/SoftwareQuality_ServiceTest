package edu.prydatkin.testingprydatkin.service;


/*
    @author lilbl
    @project testingPrydatkin
    @class StudentService
    @version 1.0.0
    @since 5/3/2025 - 12.44
*/

import edu.prydatkin.testingprydatkin.model.Student;
import edu.prydatkin.testingprydatkin.repository.StudentRepository;
import edu.prydatkin.testingprydatkin.request.StudentCreateRequest;
import edu.prydatkin.testingprydatkin.request.StudentUpdateRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;

    private final List<Student> students = new ArrayList<>();
    {
        students.add(new Student("Andrew", 17, "Male"));
        students.add(new Student("2","Victoria", 17, "Female"));
        students.add(new Student("3","Borys", 19, "Male"));
    }


    @PostConstruct
    public void init() {
        studentRepository.deleteAll();
        studentRepository.saveAll(students);
    }

    public List<Student> getAll() {return studentRepository.findAll();}

    public Student getById(String id) {return studentRepository.findById(id).orElse(null);}

    public Student create(Student students) {return studentRepository.save(students);}

    public Student create(StudentCreateRequest request) {
        if (studentRepository.existsByGender(request.gender())) {
            return null;
        }
        Student student = mapToStudent(request);
        student.setCreateDate(LocalDateTime.now());
        student.setUpdateDate(new ArrayList<>());
        return studentRepository.save(student);
    }

    public Student update(Student student) {return studentRepository.save(student);}

    public void deleteById(String id) {studentRepository.deleteById(id);}

    private Student mapToStudent(StudentCreateRequest request) {
        Student student = new Student(request.name(), request.age(), request.gender());
        return student;
    }

    public Student update(StudentUpdateRequest request) {
        Student studentPersisted = studentRepository.findById(request.id()).orElse(null);
        if (studentPersisted != null) {
            List<LocalDateTime> updateDates = studentPersisted.getUpdateDate();
            if (updateDates == null) {
                updateDates = new ArrayList<>();
            }
            updateDates.add(LocalDateTime.now());

            Student itemToUpdate = Student.builder()
                    .id(request.id())
                    .name(request.name())
                    .age(request.age())
                    .gender(request.gender())
                    .createDate(studentPersisted.getCreateDate())
                    .updateDate(updateDates)
                    .build();
            return studentRepository.save(itemToUpdate);
        }
        return null;
    }

}
