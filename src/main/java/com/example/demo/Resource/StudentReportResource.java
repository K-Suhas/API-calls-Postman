// src/main/java/com/example/demo/Resource/StudentReportResource.java
package com.example.demo.Resource;

import com.example.demo.DTO.ReportJobStatusDTO;
import com.example.demo.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/reports")
@CrossOrigin(origins = "http://localhost:4200")
public class StudentReportResource {

    @Autowired private ReportService reportService;

    // Start CSV report generation job
    @PostMapping("/students/start")
    public ResponseEntity<String> startReport(@RequestParam(required = false) Integer semester) {
        String jobId = reportService.startCsvReportJob(semester);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(jobId);
    }

    // Poll job status (progress bar)
    @GetMapping("/students/status/{jobId}")
    public ResponseEntity<ReportJobStatusDTO> getStatus(@PathVariable String jobId) {
        return ResponseEntity.ok(reportService.getJobStatus(jobId));
    }

    // Download CSV when ready
    @GetMapping("/students/download/{jobId}")
    public ResponseEntity<Resource> download(@PathVariable String jobId) {
        Resource res = reportService.downloadReport(jobId);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_report.csv");
        headers.setContentType(MediaType.TEXT_PLAIN);
        return ResponseEntity.ok().headers(headers).body(res);
    }
}
