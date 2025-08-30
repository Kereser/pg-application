package co.com.crediya.application.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
    basePackages = "co.com.crediya.application.usecase",
    includeFilters = {@ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCaseImp$")},
    useDefaultFilters = false)
public class UseCasesConfig {}
