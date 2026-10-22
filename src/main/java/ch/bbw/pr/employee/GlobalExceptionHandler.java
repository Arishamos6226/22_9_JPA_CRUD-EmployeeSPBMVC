package ch.bbw.pr.employee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * GlobalExceptionHandler
 * This class is responsible for handling exceptions globally.
 * @author Peter Rutschmann
 * @version 21.12.2024
 */
public class GlobalExceptionHandler {
   private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

   @ExceptionHandler(Exception.class)
   public String handleException(Exception exception, Model model) {
      logger.error("GlobalExceptionHandler.handleException: " + exception.getMessage());
      model.addAttribute("error", "An error occurred: " + exception.getMessage());
      return "error.html";
   }
}
