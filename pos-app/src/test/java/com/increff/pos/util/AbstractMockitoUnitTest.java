package com.increff.pos.util;

import com.increff.pos.config.QaConfig;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;

import javax.transaction.Transactional;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = QaConfig.class, loader = AnnotationConfigWebContextLoader.class)
@Transactional
public abstract class AbstractMockitoUnitTest {
}
