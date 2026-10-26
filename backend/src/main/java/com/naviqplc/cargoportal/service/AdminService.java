package com.naviqplc.cargoportal.service;

import com.naviqplc.cargoportal.dto.AdminRequest;
import com.naviqplc.cargoportal.dto.AdminResponse;
import com.naviqplc.cargoportal.entity.BusinessAdmin;
import com.naviqplc.cargoportal.entity.Role;
import com.naviqplc.cargoportal.entity.UserLogin;
import com.naviqplc.cargoportal.exception.DuplicateResourceException;   // NEW
import com.naviqplc.cargoportal.repository.BusinessAdminRepository;
import com.naviqplc.cargoportal.repository.UserLoginRepository;           // NEW
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.naviqplc.cargoportal.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    private BusinessAdminRepository adminRepository;
    private UserLoginRepository userLoginRepository;                     // NEW
    private PasswordEncoder passwordEncoder;

    public AdminService(BusinessAdminRepository adminRepository,
                        UserLoginRepository userLoginRepository,        // NEW
                        PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.userLoginRepository = userLoginRepository;                 // NEW
        this.passwordEncoder = passwordEncoder;
    }

    // ---------- 1. CREATE A NEW ADMIN ----------
    public AdminResponse createAdmin(AdminRequest request) {

        // NEW — Step 0: stop if something is already taken
        if (adminRepository.existsByAdminId(request.getAdminId())) {
            throw new DuplicateResourceException("Admin ID already exists: " + request.getAdminId());
        }
        if (userLoginRepository.existsByUsername(request.getAdminId())) {
            throw new DuplicateResourceException("Username already exists: " + request.getAdminId());
        }
        if (userLoginRepository.existsByEmailId(request.getEmailId())) {
            throw new DuplicateResourceException("Email ID already used: " + request.getEmailId());
        }
        if (adminRepository.existsByEmployeeNumber(request.getEmployeeNumber())) {
            throw new DuplicateResourceException("Employee number already used: " + request.getEmployeeNumber());
        }

        // Step A: make the user_login row
        UserLogin login = new UserLogin();
        login.setUsername(request.getAdminId());
        login.setEmailId(request.getEmailId());
        login.setPasswordHash(passwordEncoder.encode("User@123"));
        login.setMustChangePassword(true);   // new user must choose their own password at first sign-in
        login.setRole(Role.BUSINESS_ADMIN);
        login.setActive(true);


        // Step B: make the business_admin row
        BusinessAdmin admin = new BusinessAdmin();
        admin.setAdminId(request.getAdminId());
        admin.setAdminName(request.getAdminName());
        admin.setRegion(request.getRegion());
        admin.setBranch(request.getBranch());
        admin.setLanguage(request.getLanguage());
        admin.setEmployeeNumber(request.getEmployeeNumber());
        admin.setContactNumber(request.getContactNumber());

        // Step C: connect the two rows
        admin.setUser(login);

        // Step D: save to the database
        BusinessAdmin savedAdmin = adminRepository.save(admin);

        // Step E: send back a response (without password)
        return convertToResponse(savedAdmin);
    }

    // ---------- 2. GET ALL ADMINS ----------
    // ---------- 2. SEARCH (also used to list all) ----------
    public List<AdminResponse> searchAdmins(String adminId, String region, String employeeNumber) {

        // If a search box is empty, use "" so it matches everything
        if (adminId == null) {
            adminId = "";
        }
        if (region == null) {
            region = "";
        }
        if (employeeNumber == null) {
            employeeNumber = "";
        }

        List<BusinessAdmin> admins = adminRepository.search(adminId, region, employeeNumber);

        List<AdminResponse> responseList = new ArrayList<>();
        for (BusinessAdmin admin : admins) {
            responseList.add(convertToResponse(admin));
        }
        return responseList;
    }

    // ---------- 3. GET ONE ADMIN (for the Edit form) ----------
    public AdminResponse getAdminById(Integer adminPk) {
        BusinessAdmin admin = findAdminOrThrow(adminPk);
        return convertToResponse(admin);
    }

    // ---------- 4. UPDATE AN ADMIN ----------
    @Transactional
    public AdminResponse updateAdmin(Integer adminPk, AdminRequest request) {

        // Step A: load the existing admin (404 if not found)
        BusinessAdmin admin = findAdminOrThrow(adminPk);
        Integer userId = admin.getUser().getUserId();

        // Step B: duplicate checks — but ignore this admin's OWN values
        if (adminRepository.existsByAdminIdAndAdminPkNot(request.getAdminId(), adminPk)) {
            throw new DuplicateResourceException("Admin ID already exists: " + request.getAdminId());
        }
        if (userLoginRepository.existsByUsernameAndUserIdNot(request.getAdminId(), userId)) {
            throw new DuplicateResourceException("Username already exists: " + request.getAdminId());
        }
        if (userLoginRepository.existsByEmailIdAndUserIdNot(request.getEmailId(), userId)) {
            throw new DuplicateResourceException("Email ID already used: " + request.getEmailId());
        }
        if (adminRepository.existsByEmployeeNumberAndAdminPkNot(request.getEmployeeNumber(), adminPk)) {
            throw new DuplicateResourceException("Employee number already used: " + request.getEmployeeNumber());
        }

        // Step C: change the business_admin values
        admin.setAdminId(request.getAdminId());
        admin.setAdminName(request.getAdminName());
        admin.setRegion(request.getRegion());
        admin.setBranch(request.getBranch());
        admin.setLanguage(request.getLanguage());
        admin.setEmployeeNumber(request.getEmployeeNumber());
        admin.setContactNumber(request.getContactNumber());

        // Step D: change the user_login values (password is NOT changed)
        UserLogin login = admin.getUser();
        login.setUsername(request.getAdminId());
        login.setEmailId(request.getEmailId());
        if (request.getActive() != null) {
            login.setActive(request.getActive());
        }

        // Step E: save and return
        BusinessAdmin savedAdmin = adminRepository.save(admin);
        return convertToResponse(savedAdmin);
    }

    // ---------- 5. DELETE AN ADMIN ----------
    @Transactional
    public void deleteAdmin(Integer adminPk) {
        BusinessAdmin admin = findAdminOrThrow(adminPk);
        adminRepository.delete(admin);   // also deletes the user_login row (cascade)
    }

    // ---------- HELPER: find or throw 404 ----------
    private BusinessAdmin findAdminOrThrow(Integer adminPk) {
        Optional<BusinessAdmin> result = adminRepository.findById(adminPk);
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Admin not found with id: " + adminPk);
        }
        return result.get();
    }

    // ---------- HELPER: entity -> response ----------
    private AdminResponse convertToResponse(BusinessAdmin admin) {
        AdminResponse response = new AdminResponse();
        response.setAdminPk(admin.getAdminPk());
        response.setAdminId(admin.getAdminId());
        response.setAdminName(admin.getAdminName());
        response.setRegion(admin.getRegion());
        response.setBranch(admin.getBranch());
        response.setLanguage(admin.getLanguage());
        response.setEmployeeNumber(admin.getEmployeeNumber());
        response.setContactNumber(admin.getContactNumber());
        response.setEmailId(admin.getUser().getEmailId());
        response.setActive(admin.getUser().getActive());
        response.setCreatedAt(admin.getUser().getCreatedAt());
        return response;
    }
}