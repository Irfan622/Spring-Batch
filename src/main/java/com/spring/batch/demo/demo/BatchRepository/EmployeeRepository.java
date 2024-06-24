package com.spring.batch.demo.demo.BatchRepository;

import com.spring.batch.demo.demo.BatchEntities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
