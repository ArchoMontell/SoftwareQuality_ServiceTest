package edu.prydatkin.testingprydatkin;


/*
    @author lilbl
    @project testingPrydatkin
    @class TestingPrydatkinRepositoryTests
    @version 1.0.0
    @since 5/3/2025 - 22.37
*/

import edu.prydatkin.testingprydatkin.model.Student;
import edu.prydatkin.testingprydatkin.repository.StudentRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataMongoTest
public class TestingPrydatkinRepositoryTests {

    @Autowired
    StudentRepository underTest;

    @BeforeAll
    void BeforeAll() {}

    @BeforeEach
    void setUp() {
        Student vika = new Student("1","Victoria", 17, "Female");
        Student borya = new Student("2","Borys", 17, "Male");
        Student andrew = new Student("3","Andrew", 19, "Male");
        Student sasha = new Student("4","Sasha", 18, "Female");
        Student kirill = new Student("5","Kirril", 20, "Male");
        underTest.saveAll(List.of(vika, borya, andrew, sasha, kirill));
    }

    @AfterEach
    void tearDown() {
        List<Student> studentsToDelete = underTest.findAll().stream()
                .filter(student -> student.getAge() < 18)
                .toList();
        underTest.deleteAll(studentsToDelete);
    }

    @AfterAll
    void afterAll() {}

    @Test
    void testSetShouldContains_5_Records_ToTest() {
        List<Student> studentsToDelete = underTest.findAll().stream()
                .filter(student -> student.getAge() < 18)
                .toList();
        assertEquals(2, studentsToDelete.size());
    }

    @Test
    void shouldGiveIdForNewRecord() {
        Student maks = new Student(null, "Maks", 20, "Male");

        Student saved = underTest.save(maks);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(24, saved.getId().length());

        Student studentFromDb = underTest.findById(saved.getId()).orElse(null);
        assertNotNull(studentFromDb);
        assertEquals("Maks", studentFromDb.getName());
    }

    @Test
    void shouldFindStudentByName() {
        underTest.save(new Student(null, "Victoria", 17, "Female"));

        Student found = underTest.findAll().stream()
                .filter(student -> "Victoria".equals(student.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(found);
        assertEquals("Victoria", found.getName());
        assertEquals(17, found.getAge());
    }

    @Test
    void shouldDeleteStudentById() {
        Student sasha = underTest.findAll().stream()
                .filter(student -> "Sasha".equals(student.getName()))
                .findFirst()
                .orElseThrow();

        underTest.deleteById(sasha.getId());

        boolean stillExists = underTest.findAll().stream()
                .anyMatch(student -> "Sasha".equals(student.getName()));

        assertFalse(stillExists);
    }

    @Test
    void shouldUpdateStudentAge() {
        Student andrew = underTest.findAll().stream()
                .filter(student -> student.getName().equals("Andrew"))
                .findFirst().orElseThrow();

        andrew.setAge(21);
        underTest.save(andrew);

        Student updated = underTest.findById(andrew.getId()).orElseThrow();

        assertEquals(21, updated.getAge());
    }

    @Test
    void shouldReturnEmptyListIfNoStudentsMatchCriteria() {
        List<Student> over100 = underTest.findAll().stream()
                .filter(student -> student.getAge() > 100)
                .toList();

        assertTrue(over100.isEmpty());
    }

    @Test
    void shouldCountNumberOfStudents() {
        long count = underTest.count();
        assertEquals(5, count);
    }

    @Test
    void shouldFindAllFemales() {
        List<Student> females = underTest.findAll().stream()
                .filter(student -> "Female".equalsIgnoreCase(student.getGender()))
                .toList();

        assertEquals(2, females.size());
        assertTrue(females.stream().allMatch(student -> student.getGender().equalsIgnoreCase("Female")));
    }

    @Test
    void shouldReturnStudentsSortedByAgeAsc() {
        List<Student> sorted = underTest.findAll().stream()
                .sorted((a, b) -> Integer.compare(a.getAge(), b.getAge()))
                .toList();

        for (int i = 1; i < sorted.size(); i++) {
            assertTrue(sorted.get(i).getAge() >= sorted.get(i - 1).getAge());
        }
    }

    @Test
    void shouldDeleteAllStudentsOver18() {
        List<Student> over18 = underTest.findAll().stream()
                .filter(student -> student.getAge() > 18)
                .toList();

        underTest.deleteAll(over18);

        List<Student> remaining = underTest.findAll();

        assertTrue(remaining.stream().noneMatch(student -> student.getAge() > 18));
    }
}
