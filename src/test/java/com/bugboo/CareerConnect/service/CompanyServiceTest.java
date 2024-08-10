package com.bugboo.CareerConnect.service;


import com.bugboo.CareerConnect.domain.Company;
import com.bugboo.CareerConnect.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.CareerConnect.repository.CompanyRepository;
import com.bugboo.CareerConnect.type.exception.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class CompanyServiceTest {
    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    @BeforeEach
    public void setUp() {
        companyRepository = Mockito.mock(CompanyRepository.class);
        companyService = new CompanyService(companyRepository);
    }


    // Get company by id cases
    @Test
    public void testGetCompanyById_CompanyExists() {

        // Arrange
        int id = 1;
        Company company = new Company();
        company.setId(id);
        company.setName("Apple");
        company.setAddress("Cupertino, CA");
        when(companyRepository.findById(id)).thenReturn(Optional.of(company));

        // Act
        Company result = companyService.getCompanyById(id);

        // Assert
        assertNotNull(result);
        assertEquals(company, result);
    }

    @Test
    public void testGetCompanyById_CompanyNotExists() {

        // Arrange
        int id = 1;
        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        // Act & assert
        AppException thrown = assertThrows(AppException.class, () -> companyService.getCompanyById(id));
        assertEquals("Company not found with that id", thrown.getMessage());
        assertEquals(404, thrown.getStatusCode());
        assertEquals("fail", thrown.getStatus());
    }


    // Delete company by id cases
    @Test
    void testDeleteCompanyById_CompanyNotFound() {
        // Arrange
        int id = 1;
        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        AppException thrown = assertThrows(AppException.class, () -> companyService.deleteCompanyById(id));
        assertEquals("Company not found with that id", thrown.getMessage());
        assertEquals(400, thrown.getStatusCode());
        assertEquals("fail", thrown.getStatus());
    }

    @Test
    void testDeleteCompanyById_CompanyExists() {
        // Arrange
        int id = 1;
        Company company = new Company();
        company.setId(id);
        company.setName("Apple");
        company.setAddress("Cupertino, CA");

        when(companyRepository.findById(id)).thenReturn(Optional.of(company));

        // Act
        companyService.deleteCompanyById(id);

        // Assert
        verify(companyRepository, times(1)).delete(company);
    }


    // Create company cases
    @Test
    void testCreateCompany() {
        // Arrange
        Company company = new Company();
        company.setName("Apple");
        company.setAddress("Cupertino, CA");

        when(companyRepository.save(company)).thenReturn(company);

        // Act
        Company result = companyService.createCompany(company);

        // Assert
        assertNotNull(result, "The saved company should not be null.");
        assertEquals("Apple", result.getName(), "The company name should be 'Apple'.");
        assertEquals("Cupertino, CA", result.getAddress(), "The company address should be 'Cupertino, CA'.");
        verify(companyRepository, times(1)).save(company);
    }

    // Update company cases
    @Test
    void testUpdateCompany_CompanyNotFound() {
        // Arrange
        int id = 1;
        Company updatedCompany = new Company();
        updatedCompany.setId(id);

        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        AppException thrown = assertThrows(AppException.class, () -> companyService.updateCompany(updatedCompany), "Expected AppException to be thrown but it was not.");
        assertEquals("Company not found with that id", thrown.getMessage(), "The exception message is incorrect.");
        assertEquals(400, thrown.getStatusCode(), "The status code of the exception is incorrect.");
    }

    @Test
    void testUpdateCompany_Success_WithLogo() {
        // Arrange
        int id = 1;
        Company companyDB = new Company();
        companyDB.setId(id);
        companyDB.setName("Apple");
        companyDB.setAddress("Cupertino, CA");
        companyDB.setLogo("logo.png");

        Company updatedCompany = new Company();
        updatedCompany.setId(id);
        updatedCompany.setName("Apple Updated");
        updatedCompany.setAddress("Cupertino, CA Updated");
        updatedCompany.setLogo("logo-updated.png");

        when(companyRepository.findById(id)).thenReturn(Optional.of(companyDB));
        when(companyRepository.save(updatedCompany)).thenReturn(updatedCompany);

        // Act
        Company result = companyService.updateCompany(updatedCompany);

        // Assert
        assertNotNull(result, "The updated company should not be null.");
        assertEquals("Apple Updated", result.getName(), "The company name should be updated.");
        assertEquals("logo-updated.png", result.getLogo(), "The company logo should be updated.");
        verify(companyRepository, times(1)).save(updatedCompany);
    }

    @Test
    void testUpdateCompany_Success_NoLogo() {
        // Arrange
        int id = 1;
        Company companyDB = new Company();
        companyDB.setId(id);
        companyDB.setName("Apple");
        companyDB.setAddress("Cupertino, CA");
        companyDB.setLogo("logo.png");

        Company updatedCompany = new Company();
        updatedCompany.setId(id);
        updatedCompany.setName("Apple Updated");
        updatedCompany.setAddress("Cupertino, CA Updated");
        updatedCompany.setLogo(null);  // Logo is null

        when(companyRepository.findById(id)).thenReturn(Optional.of(companyDB));
        when(companyRepository.save(updatedCompany)).thenReturn(updatedCompany);

        // Act
        Company result = companyService.updateCompany(updatedCompany);

        // Assert
        assertNotNull(result, "The updated company should not be null.");
        assertEquals("Apple Updated", result.getName(), "The company name should be updated.");
        assertEquals("logo.png", result.getLogo(), "The company logo should be retained from the database.");
        verify(companyRepository, times(1)).save(updatedCompany);
    }
    @Test
    void testGetAllCompanies_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Company company1 = new Company();
        company1.setId(1);
        company1.setName("Apple");

        Company company2 = new Company();
        company2.setId(2);
        company2.setName("Google");

        List<Company> companyList = List.of(company1, company2);
        Page<Company> companyPage = new PageImpl<>(companyList, pageable, companyList.size());

        Specification<Company> specification = mock(Specification.class);
        when(companyRepository.findAll(specification, pageable)).thenReturn(companyPage);

        // Act
        ResponsePagingResultDTO result = companyService.getAllCompanies(specification, pageable);

        // Assert
        assertNotNull(result, "The result should not be null.");
        assertNotNull(result.getMetaData(), "MetaData should not be null.");
        assertEquals(1, result.getMetaData().getCurrentPage(), "Current page should be 1.");
        assertEquals(1, result.getMetaData().getTotalPages(), "Total pages should be 1.");
        assertEquals(2, result.getMetaData().getTotalElements(), "Total elements should be 2.");
        assertEquals(companyList, result.getResult(), "The result should match the expected list.");
        verify(companyRepository, times(1)).findAll(specification, pageable);
    }
}
