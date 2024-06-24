package com.spring.batch.demo.demo.BatchHelper;

import com.spring.batch.demo.demo.BatchEntities.Employee;
import com.opencsv.CSVWriter;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.List;
@Component
public class CsvEmployeeWriter implements ItemWriter<Employee> {
    private StringWriter stringWriter;

    public CsvEmployeeWriter() {
        this.stringWriter = new StringWriter();
    }

    @Override
    public void write(Chunk<? extends Employee> chunk) throws Exception {
        try (CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            for (Employee employee : chunk) {
                csvWriter.writeNext(new String[]{
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getEmail(),
                        employee.getCtc().toString(),
                        employee.getMonthlySalary().toString()
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to write CSV file", e);
        }
    }

    public StringWriter getStringWriter() {
        return stringWriter;
    }

}
