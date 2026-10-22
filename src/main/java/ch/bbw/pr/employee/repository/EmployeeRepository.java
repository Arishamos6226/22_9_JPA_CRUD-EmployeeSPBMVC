package ch.bbw.pr.employee.repository;

import ch.bbw.pr.employee.model.Department;
import ch.bbw.pr.employee.model.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * EmployeeRepository
 * @author Peter Rutschmann
 * @version 21.12.2024
 */
@Repository                                                //class, id-Typ
public interface EmployeeRepository extends CrudRepository<Employee, Integer>{
   List<Employee> findByLastname(String lastname);
   List<Employee> findByDepartment(Department department);
}

