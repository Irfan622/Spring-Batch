package com.spring.batch.demo.demo.BatchService;

import com.spring.batch.demo.demo.BatchEntities.Employee;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ExportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public ByteArrayResource exportData(String tableName, String format) throws Exception {
        List<Employee> employees = fetchDataFromTable(tableName);
        processEmployees(employees);

        Workbook workbook = createWorkbook(employees, format);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);

        return new ByteArrayResource(outputStream.toByteArray());
    }

    private List<Employee> fetchDataFromTable(String tableName) {
        String sql = "SELECT * FROM " + tableName;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Employee employee = new Employee();
            employee.setId(rs.getLong("id"));
            employee.setCtc(rs.getDouble("ctc"));
            employee.setEmail(rs.getString("email"));
            employee.setFirstName(rs.getString("first_name"));
            employee.setLastName(rs.getString("last_name"));
            employee.setMonthlySalary(rs.getDouble("monthly_salary"));
            // Populate other fields as needed
            return employee;
        });
    }

    private void processEmployees(List<Employee> employees) {
        for (Employee employee : employees) {
            // Decrease salary by 12%
            double newSalary = employee.getMonthlySalary() * 0.88; // 88% of original salary
            employee.setMonthlySalary(newSalary);
        }
    }

    private Workbook createWorkbook(List<Employee> employees, String format) {
        if ("xls".equalsIgnoreCase(format) || "xlsx".equalsIgnoreCase(format)) {
            return createExcelWorkbook(employees);
        } else if ("csv".equalsIgnoreCase(format)) {
            return createCsvWorkbook(employees);
        } else {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

    private Workbook createExcelWorkbook(List<Employee> employees) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Employees");
        int rowNum = 0;
        for (Employee employee : employees) {
            XSSFRow row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(employee.getId());
            row.createCell(1).setCellValue(employee.getFirstName());
            row.createCell(2).setCellValue(employee.getLastName());
            row.createCell(3).setCellValue(employee.getMonthlySalary());
            // Add more columns as needed
        }
        return workbook;
    }

    private Workbook createCsvWorkbook(List<Employee> employees) {
        // Implement CSV creation logic using a library like OpenCSV or Apache Commons CSV
        throw new UnsupportedOperationException("CSV export not implemented yet.");
    }
}
