package com.naviqplc.cargoportal.service;

import com.naviqplc.cargoportal.dto.AgentRequest;
import com.naviqplc.cargoportal.dto.AgentResponse;
import com.naviqplc.cargoportal.entity.AgentUser;
import com.naviqplc.cargoportal.entity.Role;
import com.naviqplc.cargoportal.entity.UserLogin;
import com.naviqplc.cargoportal.exception.DuplicateResourceException;
import com.naviqplc.cargoportal.exception.ResourceNotFoundException;
import com.naviqplc.cargoportal.repository.AgentUserRepository;
import com.naviqplc.cargoportal.repository.UserLoginRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentServiceTest {

    // ---------- FAKES ----------
    @Mock
    private AgentUserRepository agentRepository;

    @Mock
    private UserLoginRepository userLoginRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    // ---------- REAL service with the fakes ----------
    @InjectMocks
    private AgentService agentService;

    private AgentRequest request;

    @BeforeEach
    void setUp() {
        request = new AgentRequest();
        request.setLoginId("Agent50");
        request.setUserName("Jissy S Sabu");
        request.setLanguage("Japanese");
        request.setEmailId("jissy@example.com");
        request.setUserType("Agent 2");
        request.setPrivilege("Supervisor1");
        request.setAgentCode("5432");
        request.setCompanyName("Sky Freight Pvt Ltd");
        request.setAddress("Technopark, Trivandrum");
        request.setTelephone("0471-2700000");
        request.setActive(true);
    }

    // =============== CREATE ===============

    @Test
    void createAgent_success() {
        // GIVEN
        when(passwordEncoder.encode("User@123")).thenReturn("HASHED-PASSWORD");
        when(agentRepository.save(any(AgentUser.class))).thenReturn(sampleAgent());

        // WHEN
        AgentResponse response = agentService.createAgent(request);

        // THEN
        assertEquals("Agent50", response.getLoginId());          // login ID from user_login
        assertEquals("Jissy S Sabu", response.getUserName());    // display name from agent_user
        assertEquals("5432", response.getAgentCode());
        verify(passwordEncoder).encode("User@123");
        verify(agentRepository).save(any(AgentUser.class));
    }

    @Test
    void createAgent_duplicateLoginId_throwsError() {
        when(userLoginRepository.existsByUsername("Agent50")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> agentService.createAgent(request));
        verify(agentRepository, never()).save(any(AgentUser.class));
    }

    @Test
    void createAgent_duplicateAgentCode_throwsError() {
        when(agentRepository.existsByAgentCode("5432")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> agentService.createAgent(request));
        verify(agentRepository, never()).save(any(AgentUser.class));
    }

    // =============== GET ONE ===============

    @Test
    void getAgentById_found() {
        when(agentRepository.findById(1)).thenReturn(Optional.of(sampleAgent()));

        AgentResponse response = agentService.getAgentById(1);

        assertEquals("Agent50", response.getLoginId());
    }

    @Test
    void getAgentById_notFound_throwsError() {
        when(agentRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agentService.getAgentById(99));
    }

    // =============== UPDATE ===============

    @Test
    void updateAgent_changesPrivilege() {
        AgentUser existing = sampleAgent();
        when(agentRepository.findById(1)).thenReturn(Optional.of(existing));
        when(agentRepository.save(existing)).thenReturn(existing);

        request.setPrivilege("Supervisor3");          // the user changed this in the Edit form

        AgentResponse response = agentService.updateAgent(1, request);

        assertEquals("Supervisor3", response.getPrivilege());
    }

    // =============== DELETE ===============

    @Test
    void deleteAgent_found_deletesIt() {
        AgentUser agent = sampleAgent();
        when(agentRepository.findById(1)).thenReturn(Optional.of(agent));

        agentService.deleteAgent(1);

        verify(agentRepository).delete(agent);
    }

    // =============== HELPER ===============

    private AgentUser sampleAgent() {
        UserLogin login = new UserLogin();
        login.setUserId(20);
        login.setUsername("Agent50");
        login.setEmailId("jissy@example.com");
        login.setPasswordHash("HASHED-PASSWORD");
        login.setRole(Role.AGENT_USER);
        login.setActive(true);

        AgentUser agent = new AgentUser();
        agent.setAgentPk(1);
        agent.setUser(login);
        agent.setUserName("Jissy S Sabu");
        agent.setLanguage("Japanese");
        agent.setUserType("Agent 2");
        agent.setPrivilege("Supervisor1");
        agent.setAgentCode("5432");
        return agent;
    }
}