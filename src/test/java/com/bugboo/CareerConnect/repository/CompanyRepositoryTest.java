package com.bugboo.CareerConnect.repository;

import com.bugboo.CareerConnect.domain.Company;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CompanyRepositoryTest {
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void testFindById_Success() {
        // Arrange
        Company company = new Company();
        company.setName("Apple");
        company.setAddress("Cupertino, CA");
        company = entityManager.persist(company); // Lưu

        // Act
        Optional<Company> foundCompany = companyRepository.findById(company.getId());

        // Assert
        assertTrue(foundCompany.isPresent(), "Company should be found");
        assertEquals(company.getName(), foundCompany.get().getName(), "Company name should match");
    }

    

    @Test
    void testFindAll_WithSpecificationAndPagination() {
        // Arrange
        Company company1 = new Company();
        company1.setName("Apple");
        company1.setAddress("Cupertino, CA");
        companyRepository.save(company1);

        Company company2 = new Company();
        company2.setName("Google");
        company2.setAddress("Mountain View, CA");
        companyRepository.save(company2);

        Specification<Company> specification = (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("name"), "%Apple%");
        Pageable pageable = PageRequest.of(0, 1);

        // Act
        Page<Company> result = companyRepository.findAll(specification, pageable);

        // Assert
        assertEquals(1, result.getTotalElements(), "Total elements should be 1");
        assertEquals(1, result.getContent().size(), "Page content size should be 1");
        assertEquals("Apple", result.getContent().get(0).getName(), "Company name should be Apple");
    }

}
