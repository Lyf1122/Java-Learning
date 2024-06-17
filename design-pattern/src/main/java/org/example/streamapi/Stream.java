package org.example.streamapi;

import org.example.dto.Student;

import java.util.ArrayList;
import java.util.List;

public class Stream {

    private List<Student> generateStudentList() {
        List<Student> students = new ArrayList<>();
        students.add(new Student("John", 20));
        students.add(new Student("Jane", 22));
        students.add(new Student("Jim", 21));
        students.add(new Student("Joe", 23));
        students.add(new Student("Jill", 24));
       return students;
   }

   public void filterStudentsByAge() {
       List<Student> students = generateStudentList();
       students.stream()
               .filter(student -> student.age() > 20)
               .forEach(student -> System.out.println(student.name()));
   }

   public List<Student> mapStudentsByAge() {
        List<Student> students = generateStudentList();
        return students.stream()
                .map(student -> new Student(student.name(), student.age() + 1))
                .toList();
   }

}
