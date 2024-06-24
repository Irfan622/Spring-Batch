package com.spring.batch.demo.demo.Config;

import com.spring.batch.demo.demo.BatchEntities.Employee;
import com.spring.batch.demo.demo.BatchHelper.ExcelEmployeeReader;
import com.spring.batch.demo.demo.BatchHelper.WorkbookProcessor;
import com.spring.batch.demo.demo.BatchRepository.EmployeeRepository;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "com.spring.batch.demo.demo")
public class BatchConfig {

    private static final Logger logger = LoggerFactory.getLogger(BatchConfig.class);

    @Autowired
    private WorkbookProcessor workbookProcessor;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public Job excelToDatabaseJob() {
        logger.info("Creating 'excelToDatabaseJob' job bean.");
        return new JobBuilder("excelToDatabaseJob", jobRepository)
                .start(excelToDatabaseStep())
                .build();
    }

    @Bean
    public Step excelToDatabaseStep() {
        logger.info("Creating 'step1' step bean.");
        return new StepBuilder("excelToDatabaseStep", jobRepository)
                .<Employee, Employee>chunk(200, transactionManager)
                .reader(excelEmployeeReader())
                .processor(processor())
                .writer(databaseItemWriter())
                .build();
    }

    @Bean
    public ExcelEmployeeReader excelEmployeeReader() {
        logger.info("Creating 'excelEmployeeReader' bean.");
        ExcelEmployeeReader reader = new ExcelEmployeeReader();
        reader.setWorkbookProcessor(workbookProcessor);
        return reader;
    }

    @Bean
    public ItemProcessor<Employee, Employee> processor() {
        logger.info("Creating 'processor' bean.");
        return employee -> {
            Double monthlySalary = employee.getCtc() / 12;
            employee.setMonthlySalary(monthlySalary);
            return employee;
        };
    }

    @Bean
    public ItemWriter<Employee> databaseItemWriter() {
        logger.info("Creating 'databaseItemWriter' bean.");
        return items -> {
            employeeRepository.saveAll(items);
        };
    }

    @Bean
    public Job exportDataFromDatabaseJob() {
        return new JobBuilder("exportDataFromDatabase",jobRepository)
                .start(exportDataFromDatabaseStep())
                .build();
    }

    @Bean
    public Step exportDataFromDatabaseStep() {
        return new StepBuilder("exportDataFromDatabaseStep",jobRepository)
                .<Employee, Employee>chunk(100,transactionManager) // Adjust chunk size as per your needs
                .reader(employeeReaderForExport())
                .processor(employeeProcessorForExport())
                .writer(employeeWriterForExport())
                .build();
    }

    @Bean
    public ItemReader<Employee> employeeReaderForExport() {
        return new JpaPagingItemReaderBuilder<Employee>()
                .name("employeeReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT e FROM Employee e")
                .pageSize(100)
                .build();
    }

    @Bean
    public ItemProcessor<Employee, Employee> employeeProcessorForExport() {
        return employee -> {
            // Example: Decrease salary by 12%
            double newSalary = employee.getMonthlySalary() * 0.88; // 88% of original salary
            employee.setMonthlySalary(newSalary);
            return employee;
        };
    }

    @Bean
    public ItemWriter<Employee> employeeWriterForExport() {
        FlatFileItemWriter<Employee> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("target/employees.csv")); // Output file location
        writer.setLineAggregator(new PassThroughLineAggregator<>()); // Simple line aggregator
        return writer;
    }
}
