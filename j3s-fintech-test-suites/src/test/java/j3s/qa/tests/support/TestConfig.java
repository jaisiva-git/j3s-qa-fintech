package j3s.qa.tests.support;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "j3s.qa.tests"
})
public class TestConfig {
}
