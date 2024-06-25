package com.spring.batch.demo.demo.BatchController;

import com.spring.batch.demo.demo.BatchHelper.WorkbookProcessor;
import com.spring.batch.demo.demo.BatchService.BatchService;
import com.spring.batch.demo.demo.BatchService.ExportService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/batch")
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private BatchService batchService; // New service layer

    @Autowired
    private ExportService exportService;

    @PostMapping("/upload/{id}")
    public ResponseEntity<Map<String, Object>> uploadExcel(@RequestParam("file") MultipartFile file, @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            batchService.processJob(file, id);
            response.put("statusMessage", "File uploaded and processing started.");
            response.put("statusCode", 200);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("statusMessage", "Failed to process upload: " + e.getMessage());
            response.put("statusCode", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/employees")
    public ResponseEntity<Map<String, Object>> exportEmployees(@RequestParam("format") String format) {
        Map<String, Object> response = new HashMap<>();
        try {
            batchService.exportData(format);
            response.put("statusMessage", "Export job submitted successfully.");
            response.put("statusCode", 200);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("statusMessage", "Failed to submit export job: " + e.getMessage());
            response.put("statusCode", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}


