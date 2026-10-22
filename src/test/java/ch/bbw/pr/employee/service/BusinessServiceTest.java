package ch.bbw.pr.employee.service;

import ch.bbw.pr.employee.model.Department;
import ch.bbw.pr.employee.model.Employee;
import ch.bbw.pr.employee.repository.DepartmentRepository;
import ch.bbw.pr.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * BusinessServiceTest
 * This class is responsible for testing the BusinessService class.
 * @author sebastian.meier
 * @version 21.12.2024
 */
@ExtendWith(MockitoExtension.class)
class BusinessServiceTest {

    private BusinessService businessService;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    private EmployeeService employeeService;
    private Department department;
    private Employee employee;

    // Gemeinsame Testdaten vermeiden wiederholtes Erstellen derselben Objekte.
    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(employeeRepository, departmentRepository);
        businessService = new BusinessService(employeeService, departmentRepository);

        department = new Department();
        department.setId(1);
        department.setDescription("IT");

        employee = new Employee();
        employee.setId(1);
        employee.setFirstname("Anna");
        employee.setLastname("Schmid");
        employee.setDepartment(department);
    }

    @Test
    void createEmployeeWithDepartment() {
        when(departmentRepository.findByDescription("IT")).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        Employee result = businessService.createEmployeeWithDepartment("Anna", "Schmid", "IT");

        assertNotNull(result);
        assertEquals("Anna", result.getFirstname());
        assertEquals("Schmid", result.getLastname());
        assertEquals(department, result.getDepartment());

        verify(departmentRepository, times(1)).findByDescription("IT");
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void createEmployeeWithDepartmentDepartmentNotFound() {
        when(departmentRepository.findByDescription("Finance")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> businessService.createEmployeeWithDepartment("Anna", "Schmid", "Finance"));

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void findEmployeesByDepartmentDescription() {
        when(departmentRepository.findByDescription("IT")).thenReturn(Optional.of(department));
        when(employeeRepository.findByDepartment(department)).thenReturn(List.of(employee));

        List<Employee> result = businessService.findEmployeesByDepartmentDescription("IT");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Anna", result.get(0).getFirstname());
        assertEquals("Schmid", result.get(0).getLastname());
        assertEquals("IT", result.get(0).getDepartment().getDescription());

        verify(departmentRepository, times(1)).findByDescription("IT");
        verify(employeeRepository, times(1)).findByDepartment(department);
    }

    @Test
    void findEmployeesByDepartmentDescriptionDepartmentNotFound() {
        when(departmentRepository.findByDescription("Finance")).thenReturn(Optional.empty());

        List<Employee> result = businessService.findEmployeesByDepartmentDescription("Finance");

        assertTrue(result.isEmpty());
        verify(employeeRepository, never()).findByDepartment(any(Department.class));
    }

    @Test
    void transferEmployeeToDepartment() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(departmentRepository.findByDescription("HR")).thenReturn(Optional.of(new Department(2, "HR")));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        boolean result = businessService.transferEmployeeToDepartment(1, "HR");

        assertTrue(result);
        assertEquals("HR", employee.getDepartment().getDescription());
        assertEquals(2, employee.getDepartment().getId());

        verify(employeeRepository, times(1)).findById(1);
        verify(departmentRepository, times(1)).findByDescription("HR");
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void transferEmployeeToDepartmentEmployeeNotFound() {
        when(employeeRepository.findById(99)).thenReturn(Optional.empty());
        when(departmentRepository.findByDescription("HR")).thenReturn(Optional.of(new Department(2, "HR")));

        boolean result = businessService.transferEmployeeToDepartment(99, "HR");

        assertFalse(result);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void transferEmployeeToDepartmentDepartmentNotFound() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(departmentRepository.findByDescription("Finance")).thenReturn(Optional.empty());

        boolean result = businessService.transferEmployeeToDepartment(1, "Finance");

        assertFalse(result);
        assertEquals("IT", employee.getDepartment().getDescription());
        verify(employeeRepository, never()).save(any(Employee.class));
    }
}
