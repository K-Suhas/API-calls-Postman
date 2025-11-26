// ReportJobStatusDTO.java
package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReportJobStatusDTO {
    private String jobId;
    private int progress;    // 0–100
    private String state;    // PENDING, RUNNING, READY, FAILED
    private String message;
}
