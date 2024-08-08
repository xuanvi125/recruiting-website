package com.bugboo.CareerConnect.service;

import com.bugboo.CareerConnect.domain.Job;
import com.bugboo.CareerConnect.domain.Resume;
import com.bugboo.CareerConnect.domain.User;
import com.bugboo.CareerConnect.domain.dto.request.RequestApplyJobDTO;
import com.bugboo.CareerConnect.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.CareerConnect.repository.JobRepository;
import com.bugboo.CareerConnect.repository.ResumeRepository;
import com.bugboo.CareerConnect.type.apiResponse.MetaData;
import com.bugboo.CareerConnect.type.exception.AppException;
import com.bugboo.CareerConnect.utils.JwtUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final JwtUtils jwtUtils;
    private final FileUploadService fileUploadService;

    public ResumeService(ResumeRepository resumeRepository, JobRepository jobRepository, JwtUtils jwtUtils, FileUploadService fileUploadService) {
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.jwtUtils = jwtUtils;
        this.fileUploadService = fileUploadService;
    }

    public ResponsePagingResultDTO getAllResumes(Specification<Resume> specification, Pageable pageable) {
        Page<Resume> resumes = resumeRepository.findAll(specification, pageable);
        return ResponsePagingResultDTO.of(resumes);
    }

    public Resume createResume(RequestApplyJobDTO requestApplyJobDTO, MultipartFile file) throws IOException {
        if (file == null) {
            throw new AppException("Please upload resume file", 400);
        }

        User currentUser = jwtUtils.getCurrentUserLogin();
        if(currentUser == null) {
            throw new AppException("Please login to apply job",400);
        }

        Job appliedJob = jobRepository.findById(requestApplyJobDTO.getJobId())
                .orElseThrow(() -> new AppException("Invalid job id to apply",400));

        Resume resumeExist = resumeRepository.findByUserAndJob(currentUser, appliedJob).orElse(null);
        if(resumeExist != null) {
            throw new AppException("You already applied this job",400);
        }

        requestApplyJobDTO.setResumeUrl(fileUploadService.uploadSingleFile(file, "resumes").get("url").toString());

        Resume resume = new Resume();
        resume.setJob(appliedJob);
        resume.setUser(currentUser);
        resume.setUrl(requestApplyJobDTO.getResumeUrl());
        return resumeRepository.save(resume);
    }
}
