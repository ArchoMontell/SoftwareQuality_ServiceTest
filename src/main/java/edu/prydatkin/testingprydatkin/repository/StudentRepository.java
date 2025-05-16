package edu.prydatkin.testingprydatkin.repository;


/*
    @author lilbl
    @project testingPrydatkin
    @class StudentRepository
    @version 1.0.0
    @since 5/3/2025 - 12.41
*/


import edu.prydatkin.testingprydatkin.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
    public boolean existsByGender(String gender);
}
