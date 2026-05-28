package com.infotact.wms.wms.repository;


import com.infotact.wms.wms.entity.WmsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<WmsUser,Long> {
    Optional<WmsUser> findByUsername(String username);
}
