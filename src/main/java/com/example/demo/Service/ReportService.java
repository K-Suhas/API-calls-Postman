// src/main/java/com/example/demo/Service/ReportService.java
package com.example.demo.Service;

import com.example.demo.DTO.ReportJobStatusDTO;
import com.example.demo.DTO.StudentMarksheetDTO;
import org.springframework.core.io.Resource;

public interface ReportService {

    // ===== Bulk CSV job API =====
    String startCsvReportJob(Integer semester);
    ReportJobStatusDTO getJobStatus(String jobId);
    Resource downloadReport(String jobId);

    // Direct generator used internally (optional exposure)
    Resource generateCsvReport(Integer semester);

    // ===== Individual student report API =====
    StudentMarksheetDTO getIndividualReport(Long studentId, int semester);
    Resource downloadIndividualReport(Long studentId, int semester);
}
