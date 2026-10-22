package ch.bbw.pr.employee.repository;

import ch.bbw.pr.employee.model.Department;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * DepartmentRepository
 * @author Peter Rutschmann
 * @version 21.12.2024
 */
@Repository                                                    //class, id-Typ
public interface DepartmentRepository extends CrudRepository<Department, Integer> {
   Optional<Department> findByDescription(String description);
}

