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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AgentService {

    private AgentUserRepository agentRepository;
    private UserLoginRepository userLoginRepository;
    private PasswordEncoder passwordEncoder;

    public AgentService(AgentUserRepository agentRepository,
                        UserLoginRepository userLoginRepository,
                        PasswordEncoder passwordEncoder) {
        this.agentRepository = agentRepository;
        this.userLoginRepository = userLoginRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------- 1. CREATE ----------
    @Transactional
    public AgentResponse createAgent(AgentRequest request) {

        // Step 0: stop if something is already taken
        if (userLoginRepository.existsByUsername(request.getLoginId())) {
            throw new DuplicateResourceException("User ID already exists: " + request.getLoginId());
        }
        if (userLoginRepository.existsByEmailId(request.getEmailId())) {
            throw new DuplicateResourceException("Email ID already used: " + request.getEmailId());
        }
        if (agentRepository.existsByAgentCode(request.getAgentCode())) {
            throw new DuplicateResourceException("Agent code already exists: " + request.getAgentCode());
        }

        // Step A: make the user_login row
        UserLogin login = new UserLogin();
        login.setUsername(request.getLoginId());
        login.setEmailId(request.getEmailId());
        login.setPasswordHash(passwordEncoder.encode("User@123"));
        login.setMustChangePassword(true);   // new user must choose their own password at first sign-in
        login.setRole(Role.AGENT_USER);
        if (request.getActive() == null) {
            login.setActive(true);
        } else {
            login.setActive(request.getActive());
        }

        // Step B: make the agent_user row
        AgentUser agent = new AgentUser();
        agent.setUserName(request.getUserName());
        agent.setLanguage(request.getLanguage());
        agent.setUserType(request.getUserType());
        agent.setPrivilege(request.getPrivilege());
        agent.setAgentCode(request.getAgentCode());
        agent.setCompanyName(request.getCompanyName());
        agent.setAddress(request.getAddress());
        agent.setTelephone(request.getTelephone());

        // Step C: connect the two rows
        agent.setUser(login);

        // Step D: save both (cascade)
        AgentUser savedAgent = agentRepository.save(agent);

        // Step E: return the response
        return convertToResponse(savedAgent);
    }

    // ---------- 2. SEARCH (also used to list all) ----------
    public List<AgentResponse> searchAgents(String loginId, String privilege, String agentCode) {

        if (loginId == null) {
            loginId = "";
        }
        if (privilege == null) {
            privilege = "";
        }
        if (agentCode == null) {
            agentCode = "";
        }

        List<AgentUser> agents = agentRepository.search(loginId, privilege, agentCode);

        List<AgentResponse> responseList = new ArrayList<>();
        for (AgentUser agent : agents) {
            responseList.add(convertToResponse(agent));
        }
        return responseList;
    }

    // ---------- 3. GET ONE ----------
    public AgentResponse getAgentById(Integer agentPk) {
        AgentUser agent = findAgentOrThrow(agentPk);
        return convertToResponse(agent);
    }

    // ---------- 4. UPDATE ----------
    @Transactional
    public AgentResponse updateAgent(Integer agentPk, AgentRequest request) {

        // Step A: load the existing agent
        AgentUser agent = findAgentOrThrow(agentPk);
        Integer userId = agent.getUser().getUserId();

        // Step B: duplicate checks — ignore this agent's own values
        if (userLoginRepository.existsByUsernameAndUserIdNot(request.getLoginId(), userId)) {
            throw new DuplicateResourceException("User ID already exists: " + request.getLoginId());
        }
        if (userLoginRepository.existsByEmailIdAndUserIdNot(request.getEmailId(), userId)) {
            throw new DuplicateResourceException("Email ID already used: " + request.getEmailId());
        }
        if (agentRepository.existsByAgentCodeAndAgentPkNot(request.getAgentCode(), agentPk)) {
            throw new DuplicateResourceException("Agent code already exists: " + request.getAgentCode());
        }

        // Step C: change the agent_user values
        agent.setUserName(request.getUserName());
        agent.setLanguage(request.getLanguage());
        agent.setUserType(request.getUserType());
        agent.setPrivilege(request.getPrivilege());
        agent.setAgentCode(request.getAgentCode());
        agent.setCompanyName(request.getCompanyName());
        agent.setAddress(request.getAddress());
        agent.setTelephone(request.getTelephone());

        // Step D: change the user_login values (password NOT changed)
        UserLogin login = agent.getUser();
        login.setUsername(request.getLoginId());
        login.setEmailId(request.getEmailId());
        if (request.getActive() != null) {
            login.setActive(request.getActive());
        }

        // Step E: save and return
        AgentUser savedAgent = agentRepository.save(agent);
        return convertToResponse(savedAgent);
    }

    // ---------- 5. DELETE ----------
    @Transactional
    public void deleteAgent(Integer agentPk) {
        AgentUser agent = findAgentOrThrow(agentPk);
        agentRepository.delete(agent);   // also deletes the user_login row (cascade)
    }

    // ---------- HELPER: find or throw 404 ----------
    private AgentUser findAgentOrThrow(Integer agentPk) {
        Optional<AgentUser> result = agentRepository.findById(agentPk);
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Agent user not found with id: " + agentPk);
        }
        return result.get();
    }

    // ---------- HELPER: entity -> response ----------
    private AgentResponse convertToResponse(AgentUser agent) {
        AgentResponse response = new AgentResponse();
        response.setAgentPk(agent.getAgentPk());
        response.setLoginId(agent.getUser().getUsername());   // "Agent50"      from user_login
        response.setUserName(agent.getUserName());            // "Jissy S Sabu" from agent_user
        response.setLanguage(agent.getLanguage());
        response.setEmailId(agent.getUser().getEmailId());
        response.setUserType(agent.getUserType());
        response.setPrivilege(agent.getPrivilege());
        response.setAgentCode(agent.getAgentCode());
        response.setCompanyName(agent.getCompanyName());
        response.setAddress(agent.getAddress());
        response.setTelephone(agent.getTelephone());
        response.setActive(agent.getUser().getActive());
        response.setCreatedAt(agent.getUser().getCreatedAt());
        return response;
    }
}