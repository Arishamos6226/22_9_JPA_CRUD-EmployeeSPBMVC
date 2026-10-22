package ch.bbw.pr.employee.control;

import ch.bbw.pr.employee.model.Employee;
import ch.bbw.pr.employee.service.BusinessService;
import ch.bbw.pr.employee.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * ViewController
 * @author Peter Rutschmann
 * @version 21.12.2024
 */
@Controller
public class ViewController {
	private static final Logger logger = LoggerFactory.getLogger(ViewController.class);
	private final EmployeeService service;
	private final BusinessService businessService;

	// Constructor injection
	@Autowired
	public ViewController(EmployeeService service, BusinessService businessService) {
		this.service = service;
		this.businessService = businessService;
	}

	@GetMapping("/")
	public String home() {
		logger.info("Controller.home");
		return "redirect:/findAll";
	}

	@GetMapping("/findAll")
	public String findAll(Model model) {
		logger.info("Controller.findAll");
		model.addAttribute("information", service.findAllEmployees());
		return "index.html";
	}

	@GetMapping("/readById")
	public String readById(@RequestParam("id") int id, Model model) {
		logger.info("Controller.readById with id: {}", id);

		Optional<Employee> employee = service.getEmployeeById(id);
		if (employee.isPresent()) {
			model.addAttribute("information", employee.get());
		} else {
			model.addAttribute("error", "Employee with ID " + id + " not found.");
		}
		return "index.html";
	}

	@GetMapping("/readByName")
	public String readByName(@RequestParam("lastname") String lastname, Model model) {
		logger.info("Controller.readByName with lastname: {}", lastname);

		Optional<Employee> employee = service.getEmployeeByName(lastname);
		if (employee.isPresent()) {
			model.addAttribute("information", employee.get());
		} else {
			model.addAttribute("error", "Employee with lastname " + lastname + " not found.");
		}
		return "index.html";
	}

	@GetMapping("/update")
	public String update(@RequestParam("oldlastname") String oldlastname,
								@RequestParam("newlastname") String newlastname,
								Model model) {
		logger.info("Controller.update from {} to {}", oldlastname, newlastname);

		List<Employee> updatedEmployees = StreamSupport
				.stream(service.updateEmployeeLastname(oldlastname, newlastname).spliterator(), false)
				.collect(Collectors.toList());

		if (!updatedEmployees.isEmpty()) {
			model.addAttribute("information", updatedEmployees);
		} else {
			model.addAttribute("error", "No employee with lastname " + oldlastname + " found.");
		}
		return "index.html";
	}

	@GetMapping("/delete")
	public String delete(@RequestParam("lastname") String lastname, Model model) {
		logger.info("Controller.delete with lastname: {}", lastname);

		List<Employee> remainingEmployees = StreamSupport
				.stream(service.deleteEmployeeByLastname(lastname).spliterator(), false)
				.collect(Collectors.toList());

		if (!remainingEmployees.isEmpty()) {
			model.addAttribute("information", remainingEmployees);
		} else {
			model.addAttribute("error", "No employee with lastname " + lastname + " found.");
		}
		return "index.html";
	}

	@GetMapping("/createEmployeeWithDepartment")
	public String createEmployeeWithDepartment(@RequestParam("firstname") String firstname,
															 @RequestParam("lastname") String lastname,
															 @RequestParam("departmentName") String departmentName,
															 Model model) {
		logger.info("Controller.createEmployeeWithDepartment");

		try {
			Employee employee = businessService.createEmployeeWithDepartment(firstname, lastname, departmentName);
			model.addAttribute("information", employee);
		} catch (NoSuchElementException e) {
			model.addAttribute("error", e.getMessage());
		}
		return "index.html";
	}

	@GetMapping("/findEmployeesByDepartment")
	public String findEmployeesByDepartment(@RequestParam("departmentDescription") String departmentDescription, Model model) {
		logger.info("Controller.findEmployeesByDepartment with departmentDescription: {}", departmentDescription);

		List<Employee> employees = businessService.findEmployeesByDepartmentDescription(departmentDescription);
		if (!employees.isEmpty()) {
			model.addAttribute("information", employees);
		} else {
			model.addAttribute("error", "No employees found for department " + departmentDescription + ".");
		}
		return "index.html";
	}

	@GetMapping("/transferEmployee")
	public String transferEmployee(@RequestParam("employeeId") int employeeId,
											 @RequestParam("newDepartmentDescription") String newDepartmentDescription,
											 Model model) {
		logger.info("Controller.transferEmployee with employeeId: {} to newDepartmentDescription: {}",
				employeeId, newDepartmentDescription);

		boolean success = businessService.transferEmployeeToDepartment(employeeId, newDepartmentDescription);
		if (success) {
			model.addAttribute("information", "Employee transferred successfully.");
		} else {
			model.addAttribute("error", "Transfer failed. Check if the employee ID and" +
					"department name are correct.");
		}
		return "index.html";
	}
}