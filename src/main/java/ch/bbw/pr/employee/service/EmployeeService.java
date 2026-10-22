package ch.bbw.pr.employee.service;

import ch.bbw.pr.employee.model.Department;
import ch.bbw.pr.employee.model.Employee;
import ch.bbw.pr.employee.repository.DepartmentRepository;
import ch.bbw.pr.employee.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * EmployeeService
 * @author Peter Rutschmann
 * @version 21.12.2024
 */
@Service
public class EmployeeService {
   private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
   private final EmployeeRepository employeeRepo;
   private final DepartmentRepository departmentRepo;

   // Constructor injection
   @Autowired
   public EmployeeService(EmployeeRepository employeeRepo, DepartmentRepository departmentRepo) {
      this.employeeRepo = employeeRepo;
      this.departmentRepo = departmentRepo;
   }

   public Iterable<Employee> findAllEmployees() {
      return employeeRepo.findAll();
   }

   @Transactional
   public Iterable<Employee> addEmployee(String firstname, String lastname) {
      logger.info("EmployeeService.addEmployee: " + firstname + " " + lastname);
      Employee employee = new Employee();
      employee.setFirstname(firstname);
      employee.setLastname(lastname);
      employeeRepo.save(employee);
      return employeeRepo.findAll();
   }

   @Transactional
   public Iterable<Employee> addEmployeeToFirstDepartment(String firstname, String lastname) {
      logger.info("EmployeeService.addEmployeeToFirstDepartment: "+ firstname + " " + lastname);
      Department department = departmentRepo.findAll().iterator().next();
      Employee employee = new Employee();
      employee.setFirstname(firstname);
      employee.setLastname(lastname);
      employee.setDepartment(department);
      employeeRepo.save(employee);
      return employeeRepo.findAll();
   }

   public Optional<Employee> getEmployeeById(int id) {
      logger.info("EmployeeService.getEmployeeById: " + id);
      return employeeRepo.findById(id);
   }

   public Optional<Employee> getEmployeeByName(String lastname) {
      logger.info("EmployeeService.getEmployeeByName: " + lastname);
      List<Employee> list = employeeRepo.findByLastname(lastname);
      return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
   }

   @Transactional
   public Iterable<Employee> updateEmployeeLastname(String oldlastname, String newlastname) {
      logger.info("EmployeeService.updateEmployeeLastname: " + oldlastname + " -> " + newlastname);
      List<Employee> list = employeeRepo.findByLastname(oldlastname);
      if (!list.isEmpty()) {
         Employee employee = list.get(0);
         employee.setLastname(newlastname);
         employeeRepo.save(employee);
      }
      return employeeRepo.findAll();
   }

   @Transactional
   public Iterable<Employee> deleteEmployeeByLastname(String lastname) {
      logger.info("EmployeeService.deleteEmployeeByLastname: " + lastname);
      List<Employee> list = employeeRepo.findByLastname(lastname);
      if (!list.isEmpty()) {
         employeeRepo.delete(list.get(0));
      }
      return employeeRepo.findAll();
   }

   public Employee saveEmployee(Employee employee) {
      logger.info("EmployeeService.saveEmployee: " + employee);
      return employeeRepo.save(employee);
   }

   public List<Employee> findEmployeesByDepartment(Department department) {
      logger.info("EmployeeService.findEmployeesByDepartment: " + department);
      return employeeRepo.findByDepartment(department);
   }
}