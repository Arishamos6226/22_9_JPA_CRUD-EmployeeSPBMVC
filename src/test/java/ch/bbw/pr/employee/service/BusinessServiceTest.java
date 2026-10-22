package ch.bbw.pr.employee.service;

import ch.bbw.pr.employee.model.Department;
import ch.bbw.pr.employee.model.Employee;
import ch.bbw.pr.employee.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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

    @InjectMocks
    private BusinessService businessService;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private EmployeeService employeeService;
    private Department department;
    private Employee employee;

    //KI gebraucht zum nicht immer wieder Instanzen neu zu schreiben
    @BeforeEach
    void setUp() {
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
        when(employeeService.saveEmployee(any(Employee.class))).thenReturn(employee);

        Employee result = businessService.createEmployeeWithDepartment("Anna", "Schmid", "IT");

        assertNotNull(result);
        assertEquals("Anna", result.getFirstname());
        assertEquals("Schmid", result.getLastname());
        assertEquals(department, result.getDepartment());

        verify(departmentRepository, times(1)).findByDescription("IT");
        verify(employeeService, times(1)).saveEmployee(any(Employee.class));
    }

    @Test
    void findEmployeesByDepartmentDescription() {
        when(departmentRepository.findByDescription("IT")).thenReturn(Optional.of(department));
        when(employeeService.findEmployeesByDepartment(department)).thenReturn(List.of(employee));

        List<Employee> result = businessService.findEmployeesByDepartmentDescription("IT");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Anna", result.get(0).getFirstname());

        verify(departmentRepository, times(1)).findByDescription("IT");
        verify(employeeService, times(1)).findEmployeesByDepartment(department);
    }

    @Test
    void transferEmployeeToDepartment() {
        when(employeeService.getEmployeeById(1)).thenReturn(Optional.of(employee));
        when(departmentRepository.findByDescription("HR")).thenReturn(Optional.of(new Department(2, "HR")));
        when(employeeService.saveEmployee(any(Employee.class))).thenReturn(employee);

        boolean result = businessService.transferEmployeeToDepartment(1, "HR");

        assertTrue(result);
        assertEquals("HR", employee.getDepartment().getDescription());
        assertEquals(2, employee.getDepartment().getId());

        verify(employeeService, times(1)).getEmployeeById(1);
        verify(departmentRepository, times(1)).findByDescription("HR");
        verify(employeeService, times(1)).saveEmployee(any(Employee.class));
    }
}