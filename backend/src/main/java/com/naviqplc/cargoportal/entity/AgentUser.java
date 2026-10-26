package com.naviqplc.cargoportal.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name="agent_user")
@Getter @Setter
@NoArgsConstructor

public class AgentUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agent_pk")
    private Integer agentPk;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserLogin user;

    @Column(name = "user_name", nullable = false, length = 150)
    private String userName;

    @Column(name = "language", length = 50)
    private String language;

    @Column(name = "user_type", nullable = false, length = 100)
    private String userType;

    @Column(name = "privilege", length = 100)
    private String privilege;

    @Column(name = "agent_code", nullable = false, unique = true, length = 50)
    private String agentCode;

    @Column(name = "company_name", length = 200)
    private String companyName;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "telephone", length = 20)
    private String telephone;

}
