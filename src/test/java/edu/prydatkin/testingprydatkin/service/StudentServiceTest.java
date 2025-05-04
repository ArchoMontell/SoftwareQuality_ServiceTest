package edu.prydatkin.testingprydatkin.service;

import edu.prydatkin.testingprydatkin.model.Student;
import edu.prydatkin.testingprydatkin.repository.StudentRepository;
import edu.prydatkin.testingprydatkin.request.StudentCreateRequest;
import edu.prydatkin.testingprydatkin.request.StudentUpdateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
    @author lilbl
    @project testingPrydatkin
    @class StudentServiceTest
    @version 1.0.0
    @since 5/4/2025 - 19.46
*/
    
@SpringBootTest
class StudentServiceTest {

    @Autowired
    private StudentRepository repository;

    @Autowired
    private StudentService underTest;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void testCreateStudentWithDtoSetsCorrectFields() {
        StudentCreateRequest request = new StudentCreateRequest("Vovan", 19, "Male");
        Student created = underTest.create(request);
        assertEquals("Vovan", created.getName());
        assertEquals(19, created.getAge());
        assertEquals("Male", created.getGender());
    }

    @Test
    void testCreateStudentWithDtoSetsCreateDate() {
        StudentCreateRequest request = new StudentCreateRequest("Vovan", 19, "Male");
        Student created = underTest.create(request);
        assertNotNull(created.getCreateDate());
    }

    @Test
    void testCreateStudentDirectly() {
        Student student = new Student("TestUser", 20, "Female");
        Student created = underTest.create(student);
        assertNotNull(created.getId());
        assertEquals("TestUser", created.getName());
    }

    @Test
    void testCreateStudentWithNullNameThrows() {
        //given
        StudentCreateRequest request = new StudentCreateRequest(null, 19, "Male");

        //when & then
        assertThrows(IllegalArgumentException.class, () -> underTest.create(request));
    }

    @Test
    void testCreateStudentWithEmptyNameThrows() {
        assertThrows(Exception.class, () -> {
            underTest.create(new StudentCreateRequest("", 18, "Male"));
        });
    }

    @Test
    void testGetAllReturnsInsertedStudents() {
        underTest.create(new Student("TestUser", 20, "Male"));
        List<Student> all = underTest.getAll();
        assertEquals(1, all.size());
    }

    @Test
    void testGetByIdReturnsCorrectStudent() {
        Student student = underTest.create(new Student("Anna", 22, "Female"));
        Student found = underTest.getById(student.getId());
        assertEquals("Anna", found.getName());
    }

    @Test
    void testGetByIdReturnsNullIfNotFound() {
        assertNull(underTest.getById("nonexistent-id"));
    }

    @Test
    void testUpdateStudentDirectly() {
        Student student = underTest.create(new Student("Tom", 25, "Male"));
        student.setName("Tommy");
        Student updated = underTest.update(student);
        assertEquals("Tommy", updated.getName());
    }

    @Test
    void testUpdateWithDtoChangesFields() {
        Student original = underTest.create(new Student("Original", 18, "Male"));
        StudentUpdateRequest request = new StudentUpdateRequest(original.getId(), "Updated", 20, "Female");
        Student updated = underTest.update(request);
        assertEquals("Updated", updated.getName());
        assertEquals(20, updated.getAge());
    }

    @Test
    void testUpdateWithDtoAddsUpdateDate() {
        Student original = underTest.create(new Student("Bill", 23, "Male"));
        StudentUpdateRequest request = new StudentUpdateRequest(original.getId(), "Billy", 24, "Male");
        Student updated = underTest.update(request);
        assertEquals(1, updated.getUpdateDate().size());
    }

    @Test
    void testUpdateWithDtoReturnsNullIfNotFound() {
        StudentUpdateRequest request = new StudentUpdateRequest("bad-id", "Ghost", 30, "Other");
        assertNull(underTest.update(request));
    }

    @Test
    void testDeleteByIdRemovesStudent() {
        Student student = underTest.create(new Student("DeleteMe", 19, "Male"));
        underTest.deleteById(student.getId());
        assertNull(underTest.getById(student.getId()));
    }

    @Test
    void testDeleteByIdWithNonExistingIdDoesNotThrow() {
        assertDoesNotThrow(() -> underTest.deleteById("nonexistent"));
    }

    @Test
    void testInitMethodAddsThreeStudents() {
        underTest.init();
        assertEquals(3, repository.findAll().size());
    }

    @Test
    void testCreatedStudentHasEmptyUpdateDate() {
        StudentCreateRequest request = new StudentCreateRequest("Fresh", 21, "Female");
        Student created = underTest.create(request);
        assertTrue(created.getUpdateDate().isEmpty());
    }

    @Test
    void testMultipleUpdatesAddToUpdateDateList() {
        Student original = underTest.create(new Student("Chain", 22, "Male"));
        String id = original.getId();

        underTest.update(new StudentUpdateRequest(id, "Chain1", 23, "Male"));
        underTest.update(new StudentUpdateRequest(id, "Chain2", 24, "Male"));

        Student updated = underTest.getById(id);
        assertEquals(2, updated.getUpdateDate().size());
    }

    @Test
    void testMapToStudentCreatesCorrectStudent() {
        StudentCreateRequest request = new StudentCreateRequest("Mapped", 18, "Other");
        Student student = underTest.create(request);
        assertEquals("Mapped", student.getName());
        assertEquals(18, student.getAge());
    }

    @Test
    void testGetByIdIsNullSafe() {
        assertDoesNotThrow(() -> underTest.getById("missing-id"));
    }

    @Test
    void whenStudentCreated_thenCanBeRetrievedById() {
        // given
        StudentCreateRequest request = new StudentCreateRequest("Olga", 22, "Female");
        // when
        Student created = underTest.create(request);
        Student found = underTest.getById(created.getId());
        // then
        assertNotNull(found);
        assertEquals("Olga", found.getName());
        assertEquals(22, found.getAge());
        assertEquals("Female", found.getGender());
    }
}