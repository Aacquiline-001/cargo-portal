package com.naviqplc.cargoportal.repository;

import com.naviqplc.cargoportal.entity.AgentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgentUserRepository extends JpaRepository<AgentUser, Integer> {

    boolean existsByAgentCode(String agentCode);

    boolean existsByAgentCodeAndAgentPkNot(String agentCode, Integer agentPk);

    @Query("""
            SELECT a FROM AgentUser a
            WHERE LOWER(a.user.username) LIKE LOWER(CONCAT('%', :loginId, '%'))
              AND LOWER(COALESCE(a.privilege, '')) LIKE LOWER(CONCAT('%', :privilege, '%'))
              AND LOWER(a.agentCode) LIKE LOWER(CONCAT('%', :agentCode, '%'))
            ORDER BY a.agentPk DESC
            """)
    List<AgentUser> search(@Param("loginId") String loginId,
                           @Param("privilege") String privilege,
                           @Param("agentCode") String agentCode);
}