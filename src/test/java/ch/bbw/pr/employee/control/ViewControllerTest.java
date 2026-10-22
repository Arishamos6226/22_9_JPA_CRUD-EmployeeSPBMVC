package ch.bbw.pr.employee.control;

import ch.bbw.pr.employee.model.Department;
import ch.bbw.pr.employee.model.Employee;
import ch.bbw.pr.employee.repository.DepartmentRepository;
import ch.bbw.pr.employee.repository.EmployeeRepository;
import ch.bbw.pr.employee.service.BusinessService;
import ch.bbw.pr.employee.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ViewControllerTest {

   @Mock
   private EmployeeRepository employeeRepository;
   @Mock
   private DepartmentRepository departmentRepository;

   private MockMvc mockMvc;
   private Employee employee;
   private Department department;

   @BeforeEach
   void setUp() {
      EmployeeService employeeService = new EmployeeService(employeeRepository, departmentRepository);
      BusinessService businessService = new BusinessService(employeeService, departmentRepository);
      ViewController controller = new ViewController(employeeService, businessService);
      mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

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
   void findAllShowsEmployees() throws Exception {
      when(employeeRepository.findAll()).thenReturn(List.of(employee));

      ModelAndView result = mockMvc.perform(get("/findAll"))
            .andExpect(status().isOk())
            .andReturn().getModelAndView();

      assertNotNull(result);
      assertEquals("index.html", result.getViewName());
      List<?> employees = (List<?>) result.getModel().get("information");
      assertEquals(1, employees.size());
      Employee actual = (Employee) employees.get(0);
      assertEquals(1, actual.getId());
      assertEquals("Anna", actual.getFirstname());
      assertEquals("Schmid", actual.getLastname());
      assertEquals("IT", actual.getDepartment().getDescription());
      verify(employeeRepository).findAll();
   }

   @Test
   void readByIdShowsEmployee() throws Exception {
      when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));

      ModelAndView result = mockMvc.perform(get("/readById").param("id", "1"))
            .andExpect(status().isOk())
            .andReturn().getModelAndView();

      assertNotNull(result);
      Employee actual = (Employee) result.getModel().get("information");
      assertEquals("Anna", actual.getFirstname());
      assertEquals("Schmid", actual.getLastname());
      assertEquals(1, actual.getDepartment().getId());
      assertEquals("IT", actual.getDepartment().getDescription());
      assertFalse(result.getModel().containsKey("error"));
   }

   @Test
   void readByNameShowsNotFoundError() throws Exception {
      when(employeeRepository.findByLastname("Unbekannt")).thenReturn(List.of());

      ModelAndView result = mockMvc.perform(get("/readByName").param("lastname", "Unbekannt"))
            .andExpect(status().isOk())
            .andReturn().getModelAndView();

      assertNotNull(result);
      assertEquals("Employee with lastname Unbekannt not found.", result.getModel().get("error"));
      assertFalse(result.getModel().containsKey("information"));
      verify(employeeRepository).findByLastname("Unbekannt");
   }

   @Test
   void findEmployeesByDepartmentShowsRelatedEmployees() throws Exception {
      when(departmentRepository.findByDescription("IT")).thenReturn(Optional.of(department));
      when(employeeRepository.findByDepartment(department)).thenReturn(List.of(employee));

      ModelAndView result = mockMvc.perform(get("/findEmployeesByDepartment")
                  .param("departmentDescription", "IT"))
            .andExpect(status().isOk())
            .andReturn().getModelAndView();

      assertNotNull(result);
      List<?> employees = (List<?>) result.getModel().get("information");
      assertEquals(1, employees.size());
      Employee actual = (Employee) employees.get(0);
      assertEquals("Anna", actual.getFirstname());
      assertEquals("Schmid", actual.getLastname());
      assertEquals("IT", actual.getDepartment().getDescription());
      assertFalse(result.getModel().containsKey("error"));
   }

   @Test
   void createEmployeeWithDepartmentShowsErrorWhenDepartmentIsMissing() throws Exception {
      when(departmentRepository.findByDescription("Finance")).thenReturn(Optional.empty());

      ModelAndView result = mockMvc.perform(get("/createEmployeeWithDepartment")
                  .param("firstname", "Anna")
                  .param("lastname", "Schmid")
                  .param("departmentName", "Finance"))
            .andExpect(status().isOk())
            .andReturn().getModelAndView();

      assertNotNull(result);
      assertEquals("Department with name Finance not found.", result.getModel().get("error"));
      verify(employeeRepository, never()).save(any(Employee.class));
   }
}
