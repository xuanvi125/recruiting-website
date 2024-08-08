package com.bugboo.CareerConnect.service;

import com.bugboo.CareerConnect.domain.Company;
import com.bugboo.CareerConnect.domain.Job;
import com.bugboo.CareerConnect.domain.Resume;
import com.bugboo.CareerConnect.domain.User;
import com.bugboo.CareerConnect.domain.dto.request.RequestApplyJobDTO;
import com.bugboo.CareerConnect.domain.dto.request.RequestUpdateResume;
import com.bugboo.CareerConnect.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.CareerConnect.repository.JobRepository;
import com.bugboo.CareerConnect.repository.ResumeRepository;
import com.bugboo.CareerConnect.type.apiResponse.MetaData;
import com.bugboo.CareerConnect.type.exception.AppException;
import com.bugboo.CareerConnect.utils.JwtUtils;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;
import jakarta.persistence.criteria.Join;
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
        User currentUser = jwtUtils.getCurrentUserLogin();
        Company hrCompany = currentUser.getCompany();
        Specification<Resume> companySpecification = (root, query, criteriaBuilder) -> {
            Join<Resume, Job> jobJoin = root.join("job");
            Join<Job, Company> companyJoin = jobJoin.join("company");
            return criteriaBuilder.equal(companyJoin.get("id"), hrCompany.getId());
        };
        specification = specification.and(companySpecification);
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

    public Resume updateResume(RequestUpdateResume requestUpdateResume) {
        Resume resume = resumeRepository.findById(requestUpdateResume.getId())
                .orElseThrow(() -> new AppException("Invalid resume id",400));

        if (!isResumeBelongToHR(resume)) {
            throw new AppException("You are not authorized to update this resume", 403);
        }
        resume.setStatus(requestUpdateResume.getStatus());
        return resumeRepository.save(resume);
    }

    boolean isResumeBelongToHR(Resume resume) {
        User currentUser = jwtUtils.getCurrentUserLogin();
        Company hrCompany = currentUser.getCompany();
        Company resumeCompany = resume.getJob().getCompany();
        return hrCompany.getId() == resumeCompany.getId();
    }

    public Resume getResumeById(int id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new AppException("Invalid resume id",404));
        if (!isResumeBelongToHR(resume)) {
            throw new AppException("You are not authorized to view this resume", 403);
        }
        return resume;
    }
}
