package com.spring.batch.demo.demo.BatchService;
import org.springframework.batch.core.*;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.spring.batch.demo.demo.BatchHelper.WorkbookProcessor;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.web.multipart.MultipartFile;



import java.io.InputStream;

@Service
public class BatchService {

    private static final Logger logger = LoggerFactory.getLogger(BatchService.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job excelToDatabaseJob;

    @Autowired
    private Job exportDataFromDatabaseJob;

    @Autowired
    WorkbookProcessor workbookProcessor;

    public static String fileName="";

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

    public String exportData(String format) throws Exception {
        String fileName = getFileName(format);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("fileName", fileName)
                .toJobParameters();

        jobLauncher.run(exportDataFromDatabaseJob, jobParameters);

        return fileName;
    }

    private String getFileName(String format) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        fileName="target/employees_" + timestamp + "." + format;
        return "target/employees_" + timestamp + "." + format;
    }


}
