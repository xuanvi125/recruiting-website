package com.bugboo.CareerConnect.controller;

import com.bugboo.CareerConnect.domain.Resume;
import com.bugboo.CareerConnect.domain.dto.request.job.RequestApplyJobDTO;
import com.bugboo.CareerConnect.domain.dto.request.resume.RequestUpdateResume;
import com.bugboo.CareerConnect.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.CareerConnect.service.FileUploadService;
import com.bugboo.CareerConnect.service.ResumeService;
import com.bugboo.CareerConnect.type.annotation.ApiMessage;
import com.bugboo.CareerConnect.utils.JwtUtils;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeController {
    private final ResumeService resumeService;
    private final JwtUtils jwtUtils;
    public ResumeController(ResumeService resumeService, FileUploadService fileUploadService, JwtUtils jwtUtils) {
        this.resumeService = resumeService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping
    @ApiMessage("Get all resumes successfully")
    public ResponseEntity<ResponsePagingResultDTO> getAllResumes(@Filter Specification<Resume> specification, Pageable pageable) {
        return ResponseEntity.ok(resumeService.getAllResumes(specification,pageable));
    }

    @PostMapping
    @ApiMessage("Apply job successfully")
    public ResponseEntity<Resume> createResume(@Valid RequestApplyJobDTO requestApplyJobDTO, @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
        return ResponseEntity.ok(resumeService.createResume(requestApplyJobDTO, file));
    }

    @PutMapping
    @ApiMessage("Update resume successfully")
    public ResponseEntity<Resume> updateResume(@Valid @RequestBody RequestUpdateResume requestUpdateResume) {
        return ResponseEntity.ok(resumeService.updateResume(requestUpdateResume));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get resume by id successfully")
    public ResponseEntity<Resume> getResumeById(@PathVariable int id) {
        return ResponseEntity.ok(resumeService.getResumeById(id));
    }
}
