package com.bugboo.CareerConnect.service;

import com.bugboo.CareerConnect.domain.Company;
import com.bugboo.CareerConnect.domain.Job;
import com.bugboo.CareerConnect.domain.Skill;
import com.bugboo.CareerConnect.domain.dto.request.job.RequestCreateJobDTO;
import com.bugboo.CareerConnect.domain.dto.request.job.RequestUpdateJobDTO;
import com.bugboo.CareerConnect.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.CareerConnect.event.JobCreatedEvent;
import com.bugboo.CareerConnect.repository.CompanyRepository;
import com.bugboo.CareerConnect.repository.JobRepository;
import com.bugboo.CareerConnect.repository.SkillRepository;
import com.bugboo.CareerConnect.type.exception.AppException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationEventPublisher eventPublisher;
    public JobService(JobRepository jobRepository, SkillRepository skillRepository, CompanyRepository companyRepository, ApplicationEventPublisher eventPublisher) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
        this.eventPublisher = eventPublisher;
    }

    public ResponsePagingResultDTO getAllJobs(Specification<Job> specification, Pageable pageable) {
        Page<Job> jobs = jobRepository.findAll(specification, pageable);
        return ResponsePagingResultDTO.of(jobs);
    }

    public Job getJobById(int id) {
        return jobRepository.findById(id).orElseThrow(() -> new AppException("Job not found with that id",404));
    }

    public Job createJob(RequestCreateJobDTO requestCreateJobDTO) {
        Company company = companyRepository.findById(requestCreateJobDTO.getCompanyId())
                .orElseThrow(() -> new AppException("Invalid company id",400));
        List<Skill> skillList = skillRepository.findByIdIn(requestCreateJobDTO.getSkillIds());
        if (skillList.isEmpty())
            throw new AppException("Invalid skill id",400);
        Job job = new Job();
        job.setName(requestCreateJobDTO.getName());
        job.setLocation(requestCreateJobDTO.getLocation());
        job.setSalary(requestCreateJobDTO.getSalary());
        job.setQuantity(requestCreateJobDTO.getQuantity());
        job.setLevel(requestCreateJobDTO.getLevel());
        job.setDescription(requestCreateJobDTO.getDescription());
        job.setStartDate(requestCreateJobDTO.getStartDate());
        job.setCompany(company);
        job.setSkills(skillList);
        job = jobRepository.save(job);
        // notify subscribers
        JobCreatedEvent jobCreatedEvent = new JobCreatedEvent(job);
        eventPublisher.publishEvent(jobCreatedEvent);
        return job;
    }

    public void deleteJob(int id) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new AppException("Job not found with that id",400));
        jobRepository.delete(job);
    }

    public Job updateJob(RequestUpdateJobDTO requestUpdateJobDTO) {
        Job jobDB = jobRepository.findById(requestUpdateJobDTO.getId()).orElseThrow(() -> new AppException("Job not found with that id",400));
        Company company = companyRepository.findById(requestUpdateJobDTO.getCompanyId())
                .orElseThrow(() -> new AppException("Invalid company id",400));
        List<Skill> skillList = skillRepository.findByIdIn(requestUpdateJobDTO.getSkillIds());
        if(skillList.isEmpty())
            throw new AppException("Invalid skill id",400);
        jobDB.setName(requestUpdateJobDTO.getName());
        jobDB.setLocation(requestUpdateJobDTO.getLocation());
        jobDB.setSalary(requestUpdateJobDTO.getSalary());
        jobDB.setQuantity(requestUpdateJobDTO.getQuantity());
        jobDB.setLevel(requestUpdateJobDTO.getLevel());
        jobDB.setDescription(requestUpdateJobDTO.getDescription());
        jobDB.setStartDate(requestUpdateJobDTO.getStartDate());
        jobDB.setCompany(company);
        jobDB.setSkills(skillList);
        return jobRepository.save(jobDB);
    }
}
