package com.bugboo.CareerConnect.repository;

import com.bugboo.CareerConnect.domain.Job;
import com.bugboo.CareerConnect.domain.Resume;
import com.bugboo.CareerConnect.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Integer>, JpaSpecificationExecutor<Resume> {

    Optional<Resume> findByUserAndJob(User currentUser, Job appliedJob);
}
