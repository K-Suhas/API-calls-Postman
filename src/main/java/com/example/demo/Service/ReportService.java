package com.example.demo.Service;

import com.example.demo.DTO.ReportJobStatusDTO;
import org.springframework.core.io.Resource;

public interface ReportService {
    String startCsvReportJob(Integer semester);
    ReportJobStatusDTO getJobStatus(String jobId);   // ✅ must match
    Resource downloadReport(String jobId);
    Resource generateCsvReport(Integer semester);
    com.example.demo.DTO.StudentMarksheetDTO getIndividualReport(Long studentId, int semester);
    Resource downloadIndividualReport(Long studentId, int semester);
}
