package com.spring.batch.demo.demo.BatchController;

import com.spring.batch.demo.demo.BatchHelper.WorkbookProcessor;
import com.spring.batch.demo.demo.BatchService.BatchService;
import com.spring.batch.demo.demo.BatchService.ExportService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
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
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file, @PathVariable String id) throws Exception {
        batchService.processJob(file, id);
        return ResponseEntity.ok("File uploaded and processing started.");
    }


    @GetMapping("/data")
    public ResponseEntity<Resource> exportData(@RequestParam("tableName") String tableName, @RequestParam("format") String format) {
        try {
            String fileName = tableName + "_export." + format.toLowerCase();
            ByteArrayResource resource = exportService.exportData(tableName, format);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(getContentType(format)))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getContentType(String format) {
        switch (format.toLowerCase()) {
            case "xls":
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "csv":
                return "text/csv";
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }
}


