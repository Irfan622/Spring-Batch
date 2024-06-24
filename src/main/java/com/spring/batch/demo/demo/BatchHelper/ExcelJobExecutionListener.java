//package com.spring.batch.demo.demo.BatchHelper;
//
//import com.spring.batch.demo.demo.BatchHelper.ExcelEmployeeReader;
//import com.spring.batch.demo.demo.BatchHelper.WorkbookProcessor;
//import org.apache.poi.ss.usermodel.WorkbookFactory;
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.core.JobExecutionListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//public class ExcelJobExecutionListener implements JobExecutionListener {
//
//    private final WorkbookProcessor workbookProcessor;
//    private final ExcelEmployeeReader excelEmployeeReader;
//
//    @Autowired
//    public ExcelJobExecutionListener(WorkbookProcessor workbookProcessor, ExcelEmployeeReader excelEmployeeReader) {
//        this.workbookProcessor = workbookProcessor;
//        this.excelEmployeeReader = excelEmployeeReader;
//    }
//
//    @Override
//    public void beforeJob(JobExecution jobExecution) {
//        try {
//            workbookProcessor.processWorkbook(WorkbookFactory.create(excelEmployeeReader.getInputStream())); // Ensure workbook is processed
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        excelEmployeeReader.setEmployees(workbookProcessor.getEmployees()); // Set employees in the reader
//        System.out.println("Job is about to start, setting up ExcelEmployeeReader with employees from WorkbookProcessor.");
//    }
//
//    @Override
//    public void afterJob(JobExecution jobExecution) {
//        // Perform any cleanup or final actions here if needed
//    }
//}
