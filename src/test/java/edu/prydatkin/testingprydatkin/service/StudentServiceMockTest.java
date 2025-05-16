package edu.prydatkin.testingprydatkin.service;


/*
    @author lilbl
    @project testingPrydatkin
    @class StudentServiceMockTest
    @version 1.0.0
    @since 5/16/2025 - 12.12
*/

import edu.prydatkin.testingprydatkin.model.Student;
import edu.prydatkin.testingprydatkin.repository.StudentRepository;
import edu.prydatkin.testingprydatkin.request.StudentCreateRequest;
import edu.prydatkin.testingprydatkin.request.StudentUpdateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@SpringBootTest
class StudentServiceMockTest {

    @Mock
    private StudentRepository mockRepository;

    private StudentService underTest;

    @Captor
    private ArgumentCaptor<Student> argumentCaptor;

    private StudentCreateRequest request;
    private Student student;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new StudentService(mockRepository);
    }

    @AfterEach
    void tearsDown() {

    }

    @DisplayName("Create new Student")
    @Test
    void whenInsertNewStudentAndGenderNotExistsThenOk(){
        request = new StudentCreateRequest("Sasha", 12, "Male");
        student = Student.builder()
                .name(request.name())
                .age(request.age())
                .gender(request.gender())
                .build();
        given(mockRepository.existsByGender(request.gender())).willReturn(false);
        underTest.create(request);
        then(mockRepository).should().save(argumentCaptor.capture());
        Student studentToSave = argumentCaptor.getValue();
        assertThat(studentToSave.getName()).isEqualTo(request.name());
        assertTrue(studentToSave.getCreateDate().isBefore(LocalDateTime.now()));
        assertTrue(studentToSave.getUpdateDate().isEmpty());
        verify(mockRepository).save(studentToSave);
        verify(mockRepository, times (1)).existsByGender(request.gender());
        verify(mockRepository, times (1)).save(studentToSave);
    }

    @DisplayName("Create new student with StudentCreateRequest")
    @Test
    void createNewStudent_WhenGenderNotExists_SavesStudent() {
        // given
        StudentCreateRequest request = new StudentCreateRequest("Alex", 20, "Male");
        given(mockRepository.existsByGender(request.gender())).willReturn(false);

        // when
        underTest.create(request);

        // then
        verify(mockRepository).save(argumentCaptor.capture());
        Student saved = argumentCaptor.getValue();

        assertThat(saved.getName()).isEqualTo("Alex");
        assertThat(saved.getAge()).isEqualTo(20);
        assertThat(saved.getGender()).isEqualTo("Male");
        assertThat(saved.getCreateDate()).isNotNull();
        assertThat(saved.getUpdateDate()).isNotNull();
        assertThat(saved.getUpdateDate()).isEmpty();
    }

    @DisplayName("Create student fails if gender exists")
    @Test
    void createStudent_WhenGenderExists_ReturnsNull() {
        // given
        StudentCreateRequest request = new StudentCreateRequest("Anna", 18, "Female");
        when(mockRepository.existsByGender("Female")).thenReturn(true);

        // when
        Student result = underTest.create(request);

        // then
        assertNull(result);
        verify(mockRepository, never()).save(any());
    }

    @DisplayName("Update student with DTO updates updateDate")
    @Test
    void updateStudentWithDto_WhenStudentExists_UpdatesUpdateDate() {
        // given
        Student existing = Student.builder()
                .id("123")
                .name("Old Name")
                .age(20)
                .gender("Male")
                .createDate(LocalDateTime.now().minusDays(1))
                .updateDate(new ArrayList<>())
                .build();

        StudentUpdateRequest updateRequest = new StudentUpdateRequest("123", "New Name", 21, "Male");

        when(mockRepository.findById("123")).thenReturn(Optional.of(existing));

        // when
        underTest.update(updateRequest);

        // then
        verify(mockRepository).save(argumentCaptor.capture());
        Student updated = argumentCaptor.getValue();
        assertEquals("New Name", updated.getName());
        assertEquals(21, updated.getAge());
        assertEquals(1, updated.getUpdateDate().size());
    }

    @DisplayName("Get all returns students")
    @Test
    void getAll_ReturnsListOfStudents() {
        // given
        List<Student> mockList = List.of(new Student("A", 18, "Male"));
        when(mockRepository.findAll()).thenReturn(mockList);

        // when
        List<Student> result = underTest.getAll();

        // then
        assertEquals(1, result.size());
        assertEquals("A", result.get(0).getName());
    }

    @DisplayName("Get by ID returns correct student")
    @Test
    void getById_WhenExists_ReturnsStudent() {
        // given
        Student student = new Student("1", "B", 19, "Female");
        when(mockRepository.findById("1")).thenReturn(Optional.of(student));

        // when
        Student result = underTest.getById("1");

        // then
        assertNotNull(result);
        assertEquals("B", result.getName());
    }

    @DisplayName("Get by ID returns null if not found")
    @Test
    void getById_WhenNotExists_ReturnsNull() {
        when(mockRepository.findById("999")).thenReturn(Optional.empty());

        Student result = underTest.getById("999");

        assertNull(result);
    }

    @DisplayName("Create student with full object calls repository")
    @Test
    void createStudentObject_CallsSave() {
        Student student = new Student("Dan", 22, "Male");

        underTest.create(student);

        verify(mockRepository).save(student);
    }

    @DisplayName("Update student with full object calls repository")
    @Test
    void updateStudentObject_CallsSave() {
        Student student = new Student("Dan", 22, "Male");

        underTest.update(student);

        verify(mockRepository).save(student);
    }

    @DisplayName("Delete by ID calls repository delete")
    @Test
    void deleteById_CallsRepository() {
        underTest.deleteById("id123");

        verify(mockRepository).deleteById("id123");
    }

    @DisplayName("Find all students returns list")
    @Test
    void getAll_ShouldReturnStudentList() {
        // given
        List<Student> students = List.of(
                new Student("1", "Alex", 20, "Male"),
                new Student("2", "Anna", 19, "Female")
        );
        given(mockRepository.findAll()).willReturn(students);

        // when
        List<Student> result = underTest.getAll();

        // then
        assertThat(result).hasSize(2);
        verify(mockRepository).findAll();
    }

    @DisplayName("Find student by ID when present")
    @Test
    void getById_WhenStudentExists_ShouldReturnStudent() {
        // given
        Student student = new Student("1", "Alex", 20, "Male");
        given(mockRepository.findById("1")).willReturn(java.util.Optional.of(student));

        // when
        Student result = underTest.getById("1");

        // then
        assertThat(result).isEqualTo(student);
        verify(mockRepository).findById("1");
    }

    @DisplayName("Find student by ID when not present")
    @Test
    void getById_WhenStudentDoesNotExist_ShouldReturnNull() {
        given(mockRepository.findById("1")).willReturn(java.util.Optional.empty());

        // when
        Student result = underTest.getById("1");

        // then
        assertThat(result).isNull();
        verify(mockRepository).findById("1");
    }

    @DisplayName("Delete student by ID")
    @Test
    void deleteById_ShouldCallRepository() {
        String id = "1";

        // when
        underTest.deleteById(id);

        // then
        verify(mockRepository).deleteById(id);
    }

    @DisplayName("Create student with existing Student object")
    @Test
    void create_WithStudentObject_ShouldSaveStudent() {
        Student student = new Student("1", "Alex", 20, "Male");

        underTest.create(student);

        verify(mockRepository).save(student);
    }

    @DisplayName("Update student with Student object")
    @Test
    void update_WithStudentObject_ShouldSaveStudent() {
        Student student = new Student("1", "Alex", 21, "Male");

        underTest.update(student);

        verify(mockRepository).save(student);
    }

    @DisplayName("Update student when student not found")
    @Test
    void update_WhenStudentNotFound_ShouldReturnNull() {
        StudentUpdateRequest request = new StudentUpdateRequest("123", "Alex", 21, "Male");
        given(mockRepository.findById("123")).willReturn(java.util.Optional.empty());

        Student result = underTest.update(request);

        assertThat(result).isNull();
        verify(mockRepository).findById("123");
    }

    @DisplayName("Create student maps correctly from request")
    @Test
    void create_ShouldMapRequestCorrectly() {
        StudentCreateRequest request = new StudentCreateRequest("Mila", 22, "Female");

        given(mockRepository.existsByGender("Female")).willReturn(false);
        underTest.create(request);

        verify(mockRepository).save(argumentCaptor.capture());
        Student saved = argumentCaptor.getValue();
        assertThat(saved.getName()).isEqualTo("Mila");
        assertThat(saved.getAge()).isEqualTo(22);
        assertThat(saved.getGender()).isEqualTo("Female");
    }

    @DisplayName("Update student appends update date")
    @Test
    void update_ShouldAddUpdateDate() {
        Student existing = Student.builder()
                .id("10")
                .name("Misha")
                .age(18)
                .gender("Male")
                .createDate(LocalDateTime.now().minusDays(5))
                .updateDate(new ArrayList<>())
                .build();
        given(mockRepository.findById("10")).willReturn(java.util.Optional.of(existing));
        StudentUpdateRequest request = new StudentUpdateRequest("10", "Misha", 19, "Male");

        underTest.update(request);

        verify(mockRepository).save(argumentCaptor.capture());
        Student updated = argumentCaptor.getValue();
        assertThat(updated.getUpdateDate()).hasSize(1);
    }

    @DisplayName("Init method clears and saves students")
    @Test
    void init_ShouldClearAndSaveStudents() {
        underTest.init();

        verify(mockRepository).deleteAll();
        verify(mockRepository).saveAll(anyList());
    }
}



