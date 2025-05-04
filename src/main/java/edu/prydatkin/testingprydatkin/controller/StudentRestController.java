package edu.prydatkin.testingprydatkin.controller;


/*
    @author lilbl
    @project testingPrydatkin
    @class StudentRestController
    @version 1.0.0
    @since 5/3/2025 - 13.06
*/

import edu.prydatkin.testingprydatkin.model.Student;
import edu.prydatkin.testingprydatkin.request.StudentCreateRequest;
import edu.prydatkin.testingprydatkin.request.StudentUpdateRequest;
import edu.prydatkin.testingprydatkin.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/student/")
@RequiredArgsConstructor
public class StudentRestController {

    private final StudentService studentService;

    // read all
    @GetMapping
    public List<Student> showAll() {
        return studentService.getAll();
    }

    // read one
    @GetMapping("{id}")
    public Student showOneById(@PathVariable String id) {
        return studentService.getById(id);
    }

    // create
    @PostMapping
    public Student insert(@RequestBody Student student) {
        return studentService.create(student);
    }

    @PostMapping("/dto")
    public Student insert(@RequestBody StudentCreateRequest request) {
        return studentService.create(request);
    }

    // edit
    @PutMapping
    public Student edit(@RequestBody Student student) {
        return studentService.update(student);
    }

    @PutMapping("/dto")
    public Student edit(@RequestBody StudentUpdateRequest request) {
        return studentService.update(request);
    }

    // delete
    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        studentService.deleteById(id);
    }
}
