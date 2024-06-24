package com.spring.batch.demo.demo.BatchService;

import com.opencsv.CSVParser;
import com.spring.batch.demo.demo.BatchHelper.WorkbookProcessor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;



import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class BatchService {

    private static final Logger logger = LoggerFactory.getLogger(BatchService.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job excelToDatabaseJob;

    @Autowired
    WorkbookProcessor workbookProcessor;

    public void processJob(MultipartFile file, String id) throws Exception {
        logger.info("Processing job with file: {} and id: {}", file.getOriginalFilename(), id);

        // Process file and launch job
        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        Workbook workbook = null;
        // Determine file type and handle accordingly
        if (fileName != null && (fileName.endsWith(".xls") || fileName.endsWith(".xlsx"))) {
            // For Excel files
            workbook = WorkbookFactory.create(inputStream);
        }else {
            throw new IllegalArgumentException("Unsupported file type: " + fileName);
        }
        workbookProcessor.processWorkbook(workbook);
        jobLauncher.run(excelToDatabaseJob, new JobParametersBuilder()
                .addString("fileName", file.getOriginalFilename())
                .addString("id", id)
                .toJobParameters());

        logger.info("Job launched successfully for file: {} and id: {}", file.getOriginalFilename(), id);
    }
}
