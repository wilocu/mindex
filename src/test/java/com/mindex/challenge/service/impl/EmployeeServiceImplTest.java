package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingIdURL; // URL for reporting structure

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingIdURL = "http://localhost:" + port + "/reporting/{id}"; //new URL for reporting
    }

    @Test
    public void testCreateReportingStructure() {
        Employee testBoss = new Employee(); // boss
        testBoss.setFirstName("John");
        testBoss.setLastName("Doe");
        testBoss.setDepartment("Engineering");
        testBoss.setPosition("Developer");

        Employee testEmployee = new Employee(); //sub chief
        testEmployee.setFirstName("Matt");
        testEmployee.setLastName("Smith");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        //other employees
        Employee testEmployee1 = new Employee();
        testEmployee1.setFirstName("Carl");
        testEmployee1.setLastName("Johnson");
        testEmployee1.setDepartment("Engineering");
        testEmployee1.setPosition("Developer");

        Employee testEmployee2 = new Employee();
        testEmployee2.setFirstName("Paul");
        testEmployee2.setLastName("Merkel");
        testEmployee2.setDepartment("Engineering");
        testEmployee2.setPosition("Developer");

        Employee testEmployee3 = new Employee();
        testEmployee3.setFirstName("Johnathan");
        testEmployee3.setLastName("Dover");
        testEmployee3.setDepartment("Engineering");
        testEmployee3.setPosition("Developer");

        //set the boss direct report list
        List<Employee> bossList = new ArrayList<>();
        bossList.add(testEmployee);
        bossList.add(testEmployee1);
        testBoss.setDirectReports(bossList);

        //set the sub chief direct report list
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(testEmployee2);
        employeeList.add(testEmployee3);
        testEmployee.setDirectReports(employeeList);

        // Create checks
        Employee createdBoss = restTemplate.postForEntity(employeeUrl, testBoss, Employee.class).getBody();

        assertNotNull(createdBoss.getEmployeeId());
        assertEmployeeEquivalence(testBoss, createdBoss);

        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);

        Employee createdEmployee1 = restTemplate.postForEntity(employeeUrl, testEmployee1, Employee.class).getBody();

        assertNotNull(createdEmployee1.getEmployeeId());
        assertEmployeeEquivalence(testEmployee1, createdEmployee1);

        Employee createdEmployee2 = restTemplate.postForEntity(employeeUrl, testEmployee2, Employee.class).getBody();

        assertNotNull(createdEmployee2.getEmployeeId());
        assertEmployeeEquivalence(testEmployee2, createdEmployee2);

        Employee createdEmployee3 = restTemplate.postForEntity(employeeUrl, testEmployee3, Employee.class).getBody();

        assertNotNull(createdEmployee3.getEmployeeId());
        assertEmployeeEquivalence(testEmployee3, createdEmployee3);

        // report check
        ReportingStructure testStructure = restTemplate.getForEntity(reportingIdURL, ReportingStructure.class,
                createdBoss.getEmployeeId()).getBody();

        assertEquals(createdBoss.getEmployeeId(), testStructure.getEmployee().getEmployeeId());
        assertEquals(4, testStructure.getDirectReports());

    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
