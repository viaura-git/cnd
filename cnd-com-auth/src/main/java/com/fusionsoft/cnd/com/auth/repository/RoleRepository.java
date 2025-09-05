package com.fusionsoft.cnd.com.auth.repository;

import com.fusionsoft.cnd.com.auth.domain.entity.Role;
import com.fusionsoft.cnd.com.auth.domain.type.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
	
	//Optional<Role> findByName(RoleType name);

}
