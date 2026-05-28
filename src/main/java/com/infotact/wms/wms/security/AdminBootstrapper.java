package com.infotact.wms.wms.security;

import com.infotact.wms.wms.entity.WmsUser;
import com.infotact.wms.wms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrapper implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception{
        if(userRepository.findByUsername("admin").isEmpty()){
            WmsUser admin=new WmsUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);

            System.out.println("==========================================================");
            System.out.println("[Security] Bootstrapped default admin user:");
            System.out.println("           Username: admin");
            System.out.println("           Password: admin123");
            System.out.println("==========================================================");
        }
    }

}
