package com.spring.batch.demo.demo.BatchHelper;

import com.spring.batch.demo.demo.BatchEntities.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class ExcelEmployeeReader implements ItemReader<Employee> {

    private static final Logger logger = LoggerFactory.getLogger(ExcelEmployeeReader.class);

    @Autowired
    private WorkbookProcessor workbookProcessor;

    private Iterator<Employee> employeeIterator;

    @Override
    public Employee read() {
        if (employeeIterator == null) {
            initialize();
        }

        if (employeeIterator != null && employeeIterator.hasNext()) {
            Employee nextEmployee = employeeIterator.next();
            logger.info("Reading employee: {}", nextEmployee.getFirstName());
            return nextEmployee;
        } else {
            logger.info("No more employees to read.");
            return null;
        }
    }

    private void initialize() {
        List<Employee> employees = workbookProcessor.getEmployees();
        employeeIterator = employees.iterator();
        logger.info("Initialized with {} employees.", employees.size());
    }

    public void setWorkbookProcessor(WorkbookProcessor workbookProcessor) {
        this.workbookProcessor = workbookProcessor;
    }
}
