package com.spring.batch.demo.demo.BatchHelper;

import com.spring.batch.demo.demo.BatchEntities.Employee;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class WorkbookProcessor {

    private static final Logger logger = LoggerFactory.getLogger(WorkbookProcessor.class);

    private List<Employee> employees;

    public void processWorkbook(Workbook workbook) {
        employees = new ArrayList<>();

        try {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Skip header row if present
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            logger.info("Excel file loaded and iterator set.");
            logger.info("Total rows: {}", sheet.getPhysicalNumberOfRows());

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Employee employee = new Employee();
                employee.setFirstName(row.getCell(0).getStringCellValue());
                employee.setLastName(row.getCell(1).getStringCellValue());
                employee.setEmail(row.getCell(2).getStringCellValue());
                employee.setCtc(row.getCell(3).getNumericCellValue());
                employee.setDepartment(row.getCell(5).getStringCellValue());       // Department (String)
                employee.setPosition(row.getCell(6).getStringCellValue());         // Position (String)
                employee.setPhoneNumber(row.getCell(7).getNumericCellValue());      // Phone Number (String)
                employee.setAddress(row.getCell(8).getStringCellValue());          // Address (String)
                employee.setCity(row.getCell(9).getStringCellValue());             // City (String)
                employee.setState(row.getCell(10).getStringCellValue());           // State (String)
                employee.setCountry(row.getCell(11).getStringCellValue());         // Country (String)
                employee.setZipCode(String.valueOf(row.getCell(12).getNumericCellValue()));         // Zip Code (String)
                employee.setDateOfBirth(row.getCell(13).getStringCellValue());     // Date of Birth (String, format may vary)
                employee.setHireDate(row.getCell(14).getStringCellValue());        // Hire Date (String, format may vary)
                employee.setManagerName(row.getCell(15).getStringCellValue());     // Manager Name (String)
                employees.add(employee);

                logger.debug("Processed employee: {}", employee.getFirstName());
            }

        } catch (Exception e) {
            logger.error("Failed to read Excel file", e);
            throw new RuntimeException("Failed to read Excel file", e);
        }
    }

    public List<Employee> getEmployees() {
        return employees;
    }
}
