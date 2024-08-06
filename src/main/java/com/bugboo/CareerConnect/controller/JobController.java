package com.bugboo.CareerConnect.controller;

import com.bugboo.CareerConnect.domain.Job;
import com.bugboo.CareerConnect.domain.dto.request.RequestCreateJobDTO;
import com.bugboo.CareerConnect.domain.dto.request.RequestUpdateJobDTO;
import com.bugboo.CareerConnect.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.CareerConnect.service.JobService;
import com.bugboo.CareerConnect.type.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    @ApiMessage("Get all jobs successfully")
    public ResponseEntity<ResponsePagingResultDTO> getAllJobs(@Filter Specification<Job> specification, Pageable pageable) {
        return ResponseEntity.ok(jobService.getAllJobs(specification, pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get job by id successfully")
    public ResponseEntity<Job> getJobById(@PathVariable int id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @PostMapping
    @ApiMessage("Create job successfully")
    public ResponseEntity<Job> createJob(@Valid @RequestBody RequestCreateJobDTO requestCreateJobDTO) {
        return ResponseEntity.ok(jobService.createJob(requestCreateJobDTO));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete job successfully")
    public ResponseEntity<Job> deleteJob(@PathVariable int id) {
        jobService.deleteJob(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PutMapping
    @ApiMessage("Update job successfully")
    public ResponseEntity<Job> updateJob(@Valid @RequestBody RequestUpdateJobDTO requestUpdateJobDTO) {
        return ResponseEntity.ok(jobService.updateJob(requestUpdateJobDTO));
    }
}
