package com.bugboo.CareerConnect.service;

import com.bugboo.CareerConnect.domain.Company;
import com.bugboo.CareerConnect.domain.Resume;
import com.bugboo.CareerConnect.domain.Role;
import com.bugboo.CareerConnect.domain.User;
import com.bugboo.CareerConnect.domain.dto.request.admin.RequestAdminUpdateUserDTO;
import com.bugboo.CareerConnect.domain.dto.request.user.RequestUpdatePassword;
import com.bugboo.CareerConnect.domain.dto.request.user.RequestUpdateProfile;
import com.bugboo.CareerConnect.domain.dto.response.ResponseLoginDTO;
import com.bugboo.CareerConnect.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.CareerConnect.repository.CompanyRepository;
import com.bugboo.CareerConnect.repository.ResumeRepository;
import com.bugboo.CareerConnect.repository.RoleRepository;
import com.bugboo.CareerConnect.repository.UserRepository;
import com.bugboo.CareerConnect.service.specifications.ResumeSpecification;
import com.bugboo.CareerConnect.type.exception.AppException;
import com.bugboo.CareerConnect.utils.JwtUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadService fileUploadService;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;

    public UserService(UserRepository userRepository, ResumeRepository resumeRepository, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, FileUploadService fileUploadService, RoleRepository roleRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.fileUploadService = fileUploadService;
        this.roleRepository = roleRepository;
        this.companyRepository = companyRepository;
    }

    public User getCurrentUser() {
        return jwtUtils.getCurrentUserLogin();
    }

    public ResponsePagingResultDTO getResumesByUser(Specification<Resume> specification, Pageable pageable) {
        Specification<Resume> spec = specification.and(ResumeSpecification.belongToUser(getCurrentUser()));
        return ResponsePagingResultDTO.of(resumeRepository.findAll(spec, pageable));
    }

    public ResponseLoginDTO updatePassword(RequestUpdatePassword requestUpdatePassword) {
        if (!requestUpdatePassword.isMatchPassword()) {
            throw new AppException("Password and confirm password do not match",400);
        }
        User user = getCurrentUser();
        if (!passwordEncoder.matches(requestUpdatePassword.getCurrentPassword(), user.getPassword())) {
            throw new AppException("Current password is incorrect",400);
        }

        user.setPassword(passwordEncoder.encode(requestUpdatePassword.getPassword()));
        user = userRepository.save(user);
        ResponseLoginDTO responseLoginDTO = new ResponseLoginDTO();
        responseLoginDTO.setAccessToken(jwtUtils.generateToken(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())));
        responseLoginDTO.setUser(user);
        return responseLoginDTO;
    }

    public User updateProfile(RequestUpdateProfile requestUpdateProfile) throws IOException {
        User user = getCurrentUser();
        if(requestUpdateProfile.getAvatar() != null) {
            user.setAvatar(fileUploadService.uploadSingleFile(requestUpdateProfile.getAvatar(),"avatars").get("url").toString());
        }
        user.setName(requestUpdateProfile.getName());
        return userRepository.save(user);
    }

    public ResponsePagingResultDTO getUsers(Specification<User> specification, Pageable pageable) {
        return ResponsePagingResultDTO.of(userRepository.findAll(specification, pageable));
    }

    public User updateUser(RequestAdminUpdateUserDTO requestAdminUpdateUserDTO) {
        User user = userRepository.findById(requestAdminUpdateUserDTO.getId()).orElseThrow(() -> new AppException("Invalid user id",400));
        Company company = companyRepository.findById(requestAdminUpdateUserDTO.getCompanyId()).orElseThrow(() -> new AppException("Invalid company id",400));
        Role role = roleRepository.findById(requestAdminUpdateUserDTO.getRoleId()).orElseThrow(() -> new AppException("Invalid role id",400));
        user.setCompany(company);
        user.setRole(role);
        return userRepository.save(user);
    }
}
