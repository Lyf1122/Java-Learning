package com.lyf.techtools.service.impl;

import com.lyf.techtools.TechToolsApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = TechToolsApplication.class)
@TestPropertySource(locations="classpath:application-test.yml")
@Transactional
@Rollback
public abstract class BaseServiceTest {
}
