package com.bugboo.CareerConnect.repository;

import com.bugboo.CareerConnect.domain.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer>, JpaSpecificationExecutor<Skill> {
    Page<Skill> findAll(Specification<Skill> specification, Pageable pageable);
    Skill findByName(String name);

    List<Skill> findByIdIn(int[] skillIds);
}
