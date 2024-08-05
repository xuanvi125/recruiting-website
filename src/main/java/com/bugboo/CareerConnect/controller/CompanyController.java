package com.bugboo.CareerConnect.controller;

import com.bugboo.CareerConnect.domain.Company;
import com.bugboo.CareerConnect.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.CareerConnect.service.CompanyService;
import com.bugboo.CareerConnect.service.FileUploadService;
import com.bugboo.CareerConnect.type.annotation.ApiMessage;
import com.bugboo.CareerConnect.type.exception.AppException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {
    private final CompanyService companyService;
    private final FileUploadService fileUploadService;

    @Autowired
    public CompanyController(CompanyService companyService, FileUploadService fileUploadService) {
        this.companyService = companyService;
        this.fileUploadService = fileUploadService;
    }

    @GetMapping
    @ApiMessage("get all companies successfully")
    public ResponseEntity<ResponsePagingResultDTO> getAllCompanies(@Filter Specification<Company> specification, Pageable pageable) {
        return ResponseEntity.ok(companyService.getAllCompanies(specification, pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("get company by id successfully")
    public ResponseEntity<Company> getCompanyById(@PathVariable int id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    @PostMapping
    @ApiMessage("create company successfully")
    public ResponseEntity<Company> createCompany(@Valid Company company, @RequestParam(name = "file",required = false ) MultipartFile file) throws IOException {
        if (file == null || !fileUploadService.isImage(file)){
            throw new AppException("File is not an image", 400);
        }
        company.setLogo(fileUploadService.uploadSingleFile(file, "companies").get("url").toString());
        return ResponseEntity.ok(companyService.createCompany(company));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("delete company by id successfully")
    public ResponseEntity<Company> deleteCompanyById(@PathVariable int id) {
        companyService.deleteCompanyById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PutMapping
    @ApiMessage("update company successfully")
    public ResponseEntity<Company> updateCompany(@Valid Company company, @RequestParam(name = "file",required = false ) MultipartFile file) throws IOException {
        if (file != null && fileUploadService.isImage(file)) {
            company.setLogo(fileUploadService.uploadSingleFile(file, "companies").get("url").toString());
        }
        return ResponseEntity.ok(companyService.updateCompany(company));
    }
}
