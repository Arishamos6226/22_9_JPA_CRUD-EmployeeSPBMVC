package ch.bbw.pr.employee.service;

import ch.bbw.pr.employee.model.Department;
import ch.bbw.pr.employee.model.Employee;
import ch.bbw.pr.employee.repository.DepartmentRepository;
import ch.bbw.pr.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * EmployeeServiceTest
 * Test class for EmployeeService
 * @author sebastian.meier
 * @version 21.12.2024
 * */

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

   @Mock
   private EmployeeRepository employeeRepository;

   @Mock
   private DepartmentRepository departmentRepository;

   @InjectMocks
   private EmployeeService employeeService;

   @BeforeEach
   void setUp() {
   }

   @AfterEach
   void tearDown() {
   }

   @Test
   void findAllEmployees() {
      Employee emp1 = new Employee();
      emp1.setFirstname("Anna");

      Employee emp2 = new Employee();
      emp2.setFirstname("Hans");

      when(employeeRepository.findAll()).thenReturn(java.util.List.of(emp1, emp2));

      Iterable<Employee> result = employeeService.findAllEmployees();

      assertNotNull(result);
      // Vergessen wie "Casting" funktioniert
      List<Employee> list = (List<Employee>) result;
      assertEquals(2, list.size());
      assertEquals("Anna", list.get(0).getFirstname());
      verify(employeeRepository, times(1)).findAll();
   }

   @Test
   void addEmployee() {
        Employee emp = new Employee();
        emp.setId(1);
        emp.setFirstname("Anna");
        emp.setLastname("Schmid");

        when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(emp);
        when(employeeRepository.findAll()).thenReturn(List.of(emp));

        Iterable<Employee> result = employeeService.addEmployee("Anna", "Schmid");

        assertNotNull(result);
        List<Employee> list = (List<Employee>) result;
        assertEquals(1, list.size());
        assertEquals("Anna", list.get(0).getFirstname());
        verify(employeeRepository, times(1)).save(Mockito.any(Employee.class));
        verify(employeeRepository, times(1)).findAll();

   }

   @Test
   void addEmployeeToFirstDepartment() {
      Employee emp = new Employee();
      emp.setId(1);
      emp.setFirstname("Anna");
      emp.setLastname("Schmid");

      Department dept = new Department();
      dept.setId(1);
      dept.setDescription("IT");
      emp.setDepartment(dept);

      when(departmentRepository.findAll()).thenReturn(List.of(dept));
      when(employeeRepository.save(any(Employee.class))).thenReturn(emp);
      when(employeeRepository.findAll()).thenReturn(List.of(emp));

      Iterable<Employee> result = employeeService.addEmployeeToFirstDepartment("Anna", "Schmid");

      assertNotNull(result);
      List<Employee> list = (List<Employee>) result;
      assertEquals(1, list.size());
      assertEquals("Anna", list.get(0).getFirstname());
      assertEquals("Schmid", list.get(0).getLastname());
      assertEquals(dept, list.get(0).getDepartment());

      verify(departmentRepository, times(1)).findAll();
      verify(employeeRepository, times(1)).save(any(Employee.class));
      verify(employeeRepository, times(1)).findAll();
   }

   @Test
   void getEmployeeById() {
      Employee emp = new Employee();
      emp.setId(1);
      emp.setFirstname("Anna");
      emp.setLastname("Schmid");

      when(employeeRepository.findById(1)).thenReturn(Optional.of(emp));

      Optional<Employee> result = employeeService.getEmployeeById(1);

      assertTrue(result.isPresent());
      assertEquals("Anna", result.get().getFirstname());
      assertEquals("Schmid", result.get().getLastname());
      verify(employeeRepository, times(1)).findById(1);
   }

   @Test
   void getEmployeeByName() {
      Employee emp = new Employee();
      emp.setId(1);
      emp.setFirstname("Anna");
      emp.setLastname("Schmid");

      when(employeeRepository.findByLastname("Schmid")).thenReturn(List.of(emp));

      Optional<Employee> result = employeeService.getEmployeeByName("Schmid");

      assertTrue(result.isPresent());
      assertEquals("Anna", result.get().getFirstname());
      assertEquals("Schmid", result.get().getLastname());
   }

   @Test
   void updateEmployeeLastname() {
      // Arrange
      Employee emp = new Employee();
      emp.setId(1);
      emp.setFirstname("Anna");
      emp.setLastname("Schmid");

      Employee updatedEmp = new Employee();
      updatedEmp.setId(1);
      updatedEmp.setFirstname("Anna");
      updatedEmp.setLastname("Müller");

      when(employeeRepository.findByLastname("Schmid")).thenReturn(List.of(emp));
      when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmp);
      when(employeeRepository.findAll()).thenReturn(List.of(updatedEmp));

      // Act
      Iterable<Employee> result = employeeService.updateEmployeeLastname("Schmid", "Müller");
      List<Employee> list = (List<Employee>) result;

      // Assert
      assertEquals(1, list.size());
      assertEquals("Müller", list.get(0).getLastname());

      // Verify
      verify(employeeRepository, times(1)).save(emp);
   }

   @Test
   void deleteEmployeeByLastname() {
      // Arrange
      Employee emp = new Employee();
      emp.setId(1);
      emp.setFirstname("Anna");
      emp.setLastname("Schmid");

      when(employeeRepository.findByLastname("Schmid")).thenReturn(List.of(emp));
      when(employeeRepository.findAll()).thenReturn(List.of());

      // Act
      Iterable<Employee> result = employeeService.deleteEmployeeByLastname("Schmid");
      List<Employee> list = (List<Employee>) result;

      assertEquals(0, list.size());

      verify(employeeRepository, times(1)).delete(emp);
   }
}