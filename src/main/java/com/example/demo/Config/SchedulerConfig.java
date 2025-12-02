// src/main/java/com/example/demo/config/SchedulerConfig.java
package com.example.demo.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableScheduling
@EnableAsync
public class SchedulerConfig {
    // Enables @Scheduled and @Async annotations
}
