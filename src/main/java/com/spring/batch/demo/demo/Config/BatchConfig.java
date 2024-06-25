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
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.SimplePartitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import static com.spring.batch.demo.demo.BatchService.BatchService.fileName;

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

    @Autowired
    private TaskExecutor taskExecutor;

    @Bean
    public Job excelToDatabaseJob() {
        logger.info("Creating 'excelToDatabaseJob' job bean.");
        return new JobBuilder("excelToDatabaseJob", jobRepository)
                .start(masterStep())
                .build();
    }

    @Bean
    public Step masterStep() {
        return new StepBuilder("masterStep", jobRepository)
                .partitioner("slaveStep", partitioner())
                .step(slaveStep())
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Step slaveStep() {
        return new StepBuilder("slaveStep", jobRepository)
                .<Employee, Employee>chunk(200, transactionManager)
                .reader(excelEmployeeReader())
                .processor(processor())
                .writer(databaseItemWriter())
                .build();
    }

    @Bean
    public Partitioner partitioner() {
        return new SimplePartitioner();
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
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public ItemReader<Employee> employeeReaderForExport() {
        return new JpaPagingItemReaderBuilder<Employee>()
                .name("employeeReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT e FROM employee e")
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
    @StepScope
    public FlatFileItemWriter<Employee> employeeWriterForExport() {
        return new FlatFileItemWriterBuilder<Employee>()
                .name("employeeWriter")
                .resource(new FileSystemResource(fileName))
                .lineAggregator(employee -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append(employee.getId()).append(",")
                            .append(employee.getFirstName()).append(",")
                            .append(employee.getLastName()).append(",")
                            .append(employee.getEmail()).append(",")
                            .append(employee.getCtc()).append(",")
                            .append(employee.getMonthlySalary()).append(",")
                            .append(employee.getDepartment()).append(",")
                            .append(employee.getPosition()).append(",")
                            .append(employee.getPhoneNumber()).append(",")
                            .append(employee.getAddress()).append(",")
                            .append(employee.getCity()).append(",")
                            .append(employee.getState()).append(",")
                            .append(employee.getCountry()).append(",")
                            .append(employee.getZipCode()).append(",")
                            .append(employee.getDateOfBirth()).append(",")
                            .append(employee.getHireDate()).append(",")
                            .append(employee.getManagerName()).append(",")
                            .append(employee.getEmploymentStatus()).append(",")
                            .append(employee.getJobType()).append(",")
                            .append(employee.getBankName()).append(",")
                            .append(employee.getAccountNumber()).append(",")
                            .append(employee.getEmergencyContactName()).append(",")
                            .append(employee.getEmergencyContactNumber()).append(",")
                            .append(employee.getNotes());
                    return sb.toString();
                })
                .headerCallback(writer -> writer.write("id,firstName,lastName,email,ctc,monthlySalary,department,position,phoneNumber,address,city,state,country,zipCode,dateOfBirth,hireDate,managerName,employmentStatus,jobType,bankName,accountNumber,emergencyContactName,emergencyContactNumber,notes"))
                .build();
    }

}
