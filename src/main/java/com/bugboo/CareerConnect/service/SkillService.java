package com.bugboo.CareerConnect.service;

import com.bugboo.CareerConnect.domain.Skill;
import com.bugboo.CareerConnect.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.CareerConnect.repository.SkillRepository;
import com.bugboo.CareerConnect.type.apiResponse.MetaData;
import com.bugboo.CareerConnect.type.exception.AppException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public ResponsePagingResultDTO getAllSkills(Specification<Skill> specification, Pageable pageable) {
        Page<Skill> skills = skillRepository.findAll(specification, pageable);
        return ResponsePagingResultDTO.of(skills);
    }

    public Skill createSkill(Skill skill) {
        Skill skillDB = skillRepository.findByName(skill.getName());
        if(skillDB != null) {
            throw new AppException("Skill already exists", 400);
        }
        return skillRepository.save(skill);
    }

    public Skill updateSkill(Skill skill) {
        Skill skillDB = skillRepository.findById(skill.getId()).orElseThrow(() -> new AppException("Skill not found with that id", 400));
        return skillRepository.save(skill);
    }

    public void deleteSkill(int id) {
        Skill skill = skillRepository.findById(id).orElseThrow(() -> new AppException("Skill not found with that id", 400));
        skillRepository.delete(skill);
    }
}
