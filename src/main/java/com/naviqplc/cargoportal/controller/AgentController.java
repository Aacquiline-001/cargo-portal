package com.naviqplc.cargoportal.controller;

import com.naviqplc.cargoportal.dto.AgentRequest;
import com.naviqplc.cargoportal.dto.AgentResponse;
import com.naviqplc.cargoportal.service.AgentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    // GET /api/agents                         → all agents
    // GET /api/agents?privilege=Supervisor1   → search
    @GetMapping
    public List<AgentResponse> searchAgents(
            @RequestParam(required = false) String loginId,
            @RequestParam(required = false) String privilege,
            @RequestParam(required = false) String agentCode) {
        return agentService.searchAgents(loginId, privilege, agentCode);
    }

    // GET /api/agents/3                       → one agent
    @GetMapping("/{agentPk}")
    public AgentResponse getAgentById(@PathVariable Integer agentPk) {
        return agentService.getAgentById(agentPk);
    }

    // POST /api/agents                        → create
    @PostMapping
    public AgentResponse createAgent(@Valid @RequestBody AgentRequest request) {
        return agentService.createAgent(request);
    }

    // PUT /api/agents/3                       → update
    @PutMapping("/{agentPk}")
    public AgentResponse updateAgent(@PathVariable Integer agentPk,
                                     @Valid @RequestBody AgentRequest request) {
        return agentService.updateAgent(agentPk, request);
    }

    // DELETE /api/agents/3                    → delete
    @DeleteMapping("/{agentPk}")
    public String deleteAgent(@PathVariable Integer agentPk) {
        agentService.deleteAgent(agentPk);
        return "Agent user deleted successfully";
    }
}