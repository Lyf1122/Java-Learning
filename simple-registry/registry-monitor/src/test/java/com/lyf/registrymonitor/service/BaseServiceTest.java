package com.lyf.registrymonitor.service;

import com.lyf.registrymonitor.RegistryMonitorApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = RegistryMonitorApplication.class)
@TestPropertySource(locations="classpath:application-test.properties")
public abstract class BaseServiceTest {
}
