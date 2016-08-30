package org.zalando.failsafeactuator.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.zalando.failsafeactuator.PostConstructSampleApp;
import org.zalando.failsafeactuator.TestConfiguration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(value = "classpath:application-test.properties")
@SpringApplicationConfiguration(classes = PostConstructSampleApp.class)
@WebAppConfiguration
public class FactoryPostConstructTest {

    @Autowired
    private TestConfiguration config;

    @Test
    public void instanceUnequalTest() {
        assertNotNull(config);
        assertTrue(true);
    }

    @Test
    public void registerSecondBreakerWithSameNameTest() {
        assertTrue(true);
    }
}
