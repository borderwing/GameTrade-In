package com.bankrupted.tradein.service.repository;

import com.bankrupted.tradein.service.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by lykav on 7/11/2017.
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {

    String customerRoleName = "ROLE_CUSTOMER";
    String adminRoleName = "ROLE_ADMIN";
    String verifierRoleName = "ROLE_VERIFIER";


    RoleEntity findByName(String roleName);
}
