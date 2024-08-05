package com.bugboo.CareerConnect.controller;

import com.bugboo.CareerConnect.domain.Skill;
import com.bugboo.CareerConnect.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.CareerConnect.service.SkillService;
import com.bugboo.CareerConnect.type.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/skills")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    @ApiMessage("Get all skills successfully")
    public ResponseEntity<ResponsePagingResultDTO> getAllSkills(@Filter Specification<Skill> specification, Pageable pageable) {
        return ResponseEntity.ok(skillService.getAllSkills(specification, pageable));
    }

    @PostMapping
    @ApiMessage("Create skill successfully")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) {
        return ResponseEntity.ok(skillService.createSkill(skill));
    }

    @PutMapping
    @ApiMessage("Update skill successfully")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill skill) {
        return ResponseEntity.ok(skillService.updateSkill(skill));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete skill successfully")
    public ResponseEntity<Skill> deleteSkill(@PathVariable int id) {
        skillService.deleteSkill(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
