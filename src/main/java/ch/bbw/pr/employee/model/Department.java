package ch.bbw.pr.employee.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Department
 * @author Peter Rutschmann
 * @version 21.12.2024
 */
@Entity
@Table(name = "department")
@NamedQuery(name = "Department.findAll", query = "FROM Department")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Department {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id", unique = true, nullable = false)
   private int id;

   @Column(name = "description", length = 255, nullable = false)
   private String description;
}
