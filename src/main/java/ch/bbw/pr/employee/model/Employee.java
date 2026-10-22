package ch.bbw.pr.employee.model;

import jakarta.persistence.*;
import lombok.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Employee entity
 * @author Peter Rutschmann
 * @version 21.12.2024
 */
@Entity
@Table(name = "employee")
@NamedQuery(name = "Employee.findAll", query = "FROM Employee")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee {
   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   @Column(name = "id", unique = true, nullable = false)
   private int id;

   @NotNull
   @Size(max = 50)
   @Column(name = "firstname", length = 50, nullable = false)
   private String firstname;

   @NotNull
   @Size(max = 50)
   @Column(name = "lastname", length = 50, nullable = false)
   private String lastname;

   @ManyToOne
   @JoinColumn(name = "departmentidfs")
   @EqualsAndHashCode.Exclude
   @ToString.Exclude
   private Department department;
}
