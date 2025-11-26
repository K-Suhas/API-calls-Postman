// src/main/java/com/example/demo/Service/ReportService.java
package com.example.demo.Service;

import com.example.demo.DTO.ReportJobStatusDTO;
import org.springframework.core.io.Resource;

public interface ReportService {

    // Start a simulated CSV report generation job (returns jobId)
    String startCsvReportJob(Integer semester);

    // Poll job status by jobId
    ReportJobStatusDTO getJobStatus(String jobId);

    // Download the generated CSV file when ready
    Resource downloadReport(String jobId);

    // Direct CSV generation (used internally by the async job)
    Resource generateCsvReport(Integer semester);
}
