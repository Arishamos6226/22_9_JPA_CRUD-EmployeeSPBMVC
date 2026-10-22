package ch.bbw.pr.employee.service;

import ch.bbw.pr.employee.model.Department;
import ch.bbw.pr.employee.model.Employee;
import ch.bbw.pr.employee.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * BusinessService
 * This class is responsible for business logic.
 * @author Peter Rutschmann
 * @version 21.12.2024
 */
@Service
public class BusinessService {
   private static final Logger logger = LoggerFactory.getLogger(BusinessService.class);

   private final EmployeeService employeeService;
   private final DepartmentRepository departmentRepo;

   @Autowired
   public BusinessService(EmployeeService employeeService, DepartmentRepository departmentRepo) {
      this.employeeService = employeeService;
      this.departmentRepo = departmentRepo;
   }

   /**
    * Create a new employee and assign to the specified department by name.
    */
   public Employee createEmployeeWithDepartment(String firstname, String lastname, String departmentDescription) {
      logger.info("BusinessService.createEmployeeWithDepartment");
      Optional<Department> departmentOpt = departmentRepo.findByDescription(departmentDescription);

      if (departmentOpt.isPresent()) {
         Employee employee = new Employee();
         employee.setFirstname(firstname);
         employee.setLastname(lastname);
         employee.setDepartment(departmentOpt.get());
         return employeeService.saveEmployee(employee);
      } else {
         logger.warn("BusinessService.createEmployeeWithDepartment department not found: " + departmentDescription);
         throw new NoSuchElementException("Department with name " + departmentDescription + " not found.");
      }
   }

   /**
    * Find employees by department description.
    */
   public List<Employee> findEmployeesByDepartmentDescription(String departmentDescription) {
      logger.info("BusinessService.findEmployeesByDepartmentDescription");
      Optional<Department> department = departmentRepo.findByDescription(departmentDescription);
      if (department.isPresent()) {
         return employeeService.findEmployeesByDepartment(department.get());
      }
      logger.warn("BusinessService.findEmployeesByDepartmentDescription no employees found");
      return List.of(); // Rückgabe einer leeren Liste, wenn die Abteilung nicht gefunden wird
   }

   /**
    * Transfer an employee to another department.
    */
   public boolean transferEmployeeToDepartment(int employeeId, String newDepartmentDescription) {
      logger.info("BusinessService.transferEmployeeToDepartment");
      Optional<Employee> employeeOpt = employeeService.getEmployeeById(employeeId);
      Optional<Department> departmentOpt = departmentRepo.findByDescription(newDepartmentDescription);

      if (employeeOpt.isPresent() && departmentOpt.isPresent()) {
         Employee employee = employeeOpt.get();
         employee.setDepartment(departmentOpt.get());
         employeeService.saveEmployee(employee);
         return true;
      }
      logger.error("BusinessService.transferEmployeeToDepartment transfer fails");
      return false;
   }
}
