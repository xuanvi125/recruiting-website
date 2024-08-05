package com.bugboo.CareerConnect.service;

import com.bugboo.CareerConnect.domain.Company;
import com.bugboo.CareerConnect.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.CareerConnect.repository.CompanyRepository;
import com.bugboo.CareerConnect.type.apiResponse.MetaData;
import com.bugboo.CareerConnect.type.exception.AppException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public ResponsePagingResultDTO getAllCompanies(Specification<Company> specification, Pageable pageable) {
        Page<Company> companies = companyRepository.findAll(specification, pageable);
        ResponsePagingResultDTO responsePagingResultDTO = new ResponsePagingResultDTO();
        MetaData metaData = new MetaData();
        metaData.setCurrentPage(companies.getNumber() + 1);
        metaData.setTotalPages(companies.getTotalPages());
        metaData.setTotalElements(companies.getTotalElements());
        responsePagingResultDTO.setMetaData(metaData);
        responsePagingResultDTO.setResult(companies.getContent());
        return responsePagingResultDTO;
    }

    public Company getCompanyById(int id) {
        return companyRepository.findById(id).orElseThrow(() -> new AppException("Company not found with that id", 404));
    }

    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }

    public void deleteCompanyById(int id) {
        Company company = companyRepository.findById(id).orElseThrow(() -> new AppException("Company not found with that id", 400));
        companyRepository.delete(company);
    }

    public Company updateCompany(Company company) {
        Company companyDB = companyRepository.findById(company.getId()).orElseThrow(() -> new AppException("Company not found with that id", 400));
        if (company.getLogo() == null) {
            company.setLogo(companyDB.getLogo());
        }
        return companyRepository.save(company);
    }
}
