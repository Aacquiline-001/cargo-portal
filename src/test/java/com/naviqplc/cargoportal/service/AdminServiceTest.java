package com.naviqplc.cargoportal.service;

import com.naviqplc.cargoportal.dto.AdminRequest;
import com.naviqplc.cargoportal.dto.AdminResponse;
import com.naviqplc.cargoportal.entity.BusinessAdmin;
import com.naviqplc.cargoportal.entity.Role;
import com.naviqplc.cargoportal.entity.UserLogin;
import com.naviqplc.cargoportal.exception.DuplicateResourceException;
import com.naviqplc.cargoportal.exception.ResourceNotFoundException;
import com.naviqplc.cargoportal.repository.BusinessAdminRepository;
import com.naviqplc.cargoportal.repository.UserLoginRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)     // switch Mockito on for this class
class AdminServiceTest {

    // ---------- the FAKES ----------
    @Mock
    private BusinessAdminRepository adminRepository;

    @Mock
    private UserLoginRepository userLoginRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    // ---------- the REAL service, built with the fakes above ----------
    @InjectMocks
    private AdminService adminService;

    private AdminRequest request;

    // Runs BEFORE every test: prepare a normal "Add Admin" form
    @BeforeEach
    void setUp() {
        request = new AdminRequest();
        request.setAdminId("ADMIN005");
        request.setAdminName("Jenny James");
        request.setRegion("India");
        request.setBranch("ibs-13");
        request.setLanguage("English");
        request.setEmployeeNumber("1005");
        request.setContactNumber("9876543210");
        request.setEmailId("admin005@cargoportal.com");
        request.setActive(true);
    }

    // =============== CREATE ===============

    @Test
    void createAdmin_success() {
        // GIVEN: nothing is duplicated (fakes answer false by default),
        //        the encoder scrambles the password,
        //        and saving returns a saved admin
        when(passwordEncoder.encode("User@123")).thenReturn("HASHED-PASSWORD");
        when(adminRepository.save(any(BusinessAdmin.class))).thenReturn(sampleAdmin());

        // WHEN
        AdminResponse response = adminService.createAdmin(request);

        // THEN
        assertEquals("ADMIN005", response.getAdminId());
        assertEquals("admin005@cargoportal.com", response.getEmailId());
        verify(passwordEncoder).encode("User@123");             // password was scrambled
        verify(adminRepository).save(any(BusinessAdmin.class));   // save was called once
    }

    @Test
    void createAdmin_duplicateAdminId_throwsError() {
        // GIVEN: Admin ID already exists
        when(adminRepository.existsByAdminId("ADMIN005")).thenReturn(true);

        // WHEN + THEN: createAdmin must throw DuplicateResourceException
        assertThrows(DuplicateResourceException.class, () -> adminService.createAdmin(request));

        // and nothing must be saved
        verify(adminRepository, never()).save(any(BusinessAdmin.class));
    }

    @Test
    void createAdmin_duplicateEmail_throwsError() {
        // GIVEN: email already used
        when(userLoginRepository.existsByEmailId("admin005@cargoportal.com")).thenReturn(true);

        // WHEN + THEN
        assertThrows(DuplicateResourceException.class, () -> adminService.createAdmin(request));
        verify(adminRepository, never()).save(any(BusinessAdmin.class));
    }

    // =============== GET ONE ===============

    @Test
    void getAdminById_found() {
        // GIVEN: admin 1 exists
        when(adminRepository.findById(1)).thenReturn(Optional.of(sampleAdmin()));

        // WHEN
        AdminResponse response = adminService.getAdminById(1);

        // THEN
        assertEquals("Jenny James", response.getAdminName());
    }

    @Test
    void getAdminById_notFound_throwsError() {
        // GIVEN: admin 99 does not exist (empty box)
        when(adminRepository.findById(99)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(ResourceNotFoundException.class, () -> adminService.getAdminById(99));
    }

    // =============== DELETE ===============

    @Test
    void deleteAdmin_found_deletesIt() {
        BusinessAdmin admin = sampleAdmin();
        when(adminRepository.findById(1)).thenReturn(Optional.of(admin));

        adminService.deleteAdmin(1);

        verify(adminRepository).delete(admin);     // delete was called with that admin
    }

    @Test
    void deleteAdmin_notFound_deletesNothing() {
        when(adminRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.deleteAdmin(99));
        verify(adminRepository, never()).delete(any(BusinessAdmin.class));
    }

    // =============== SEARCH ===============

    @Test
    void searchAdmins_emptyBoxes_becomeEmptyText() {
        // GIVEN: the search query called with "", "", "" returns one admin
        List<BusinessAdmin> list = new ArrayList<>();
        list.add(sampleAdmin());
        when(adminRepository.search("", "", "")).thenReturn(list);

        // WHEN: all three search boxes are empty (null)
        List<AdminResponse> result = adminService.searchAdmins(null, null, null);

        // THEN
        assertEquals(1, result.size());
    }

    // =============== HELPER ===============

    // A ready-made admin with its login, as if loaded from the database
    private BusinessAdmin sampleAdmin() {
        UserLogin login = new UserLogin();
        login.setUserId(10);
        login.setUsername("ADMIN005");
        login.setEmailId("admin005@cargoportal.com");
        login.setPasswordHash("HASHED-PASSWORD");
        login.setRole(Role.BUSINESS_ADMIN);
        login.setActive(true);

        BusinessAdmin admin = new BusinessAdmin();
        admin.setAdminPk(1);
        admin.setUser(login);
        admin.setAdminId("ADMIN005");
        admin.setAdminName("Jenny James");
        admin.setRegion("India");
        admin.setBranch("ibs-13");
        admin.setLanguage("English");
        admin.setEmployeeNumber("1005");
        return admin;
    }
}