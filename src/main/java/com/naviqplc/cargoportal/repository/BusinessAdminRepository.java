package com.naviqplc.cargoportal.repository;

import com.naviqplc.cargoportal.entity.BusinessAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BusinessAdminRepository extends JpaRepository<BusinessAdmin,Integer> {
    boolean existsByAdminId(String adminId);
    boolean existsByEmployeeNumber(String employeeNumber);
    boolean existsByAdminIdAndAdminPkNot(String adminId, Integer adminPk);
    boolean existsByEmployeeNumberAndAdminPkNot(String employeeNumber, Integer adminPk);

    @Query("""
            SELECT a FROM BusinessAdmin a
            WHERE LOWER(a.adminId) LIKE LOWER(CONCAT('%', :adminId, '%'))
              AND LOWER(a.region) LIKE LOWER(CONCAT('%', :region, '%'))
              AND LOWER(COALESCE(a.employeeNumber, '')) LIKE LOWER(CONCAT('%', :employeeNumber, '%'))
            ORDER BY a.adminPk DESC
            """)
    List<BusinessAdmin> search(@Param("adminId") String adminId,
                               @Param("region") String region,
                               @Param("employeeNumber") String employeeNumber);


}
