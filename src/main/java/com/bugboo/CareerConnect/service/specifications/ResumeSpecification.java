package com.bugboo.CareerConnect.service.specifications;

import com.bugboo.CareerConnect.domain.Company;
import com.bugboo.CareerConnect.domain.Job;
import com.bugboo.CareerConnect.domain.Resume;
import com.bugboo.CareerConnect.domain.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class ResumeSpecification {
    public static Specification<Resume> belongToCompany(int companyId) {
        return (root, query, criteriaBuilder) -> {
            Join<Resume, Job> jobJoin = root.join("job");
            Join<Job, Company> companyJoin = jobJoin.join("company");
            return criteriaBuilder.equal(companyJoin.get("id"), companyId);
        };
    }
    public static Specification<Resume> belongToUser(User user){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
    }
}
