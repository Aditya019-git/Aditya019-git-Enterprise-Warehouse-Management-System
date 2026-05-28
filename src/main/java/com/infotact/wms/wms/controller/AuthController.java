package com.infotact.wms.wms.controller;


import com.infotact.wms.wms.entity.WmsUser;
import com.infotact.wms.wms.repository.UserRepository;
import com.infotact.wms.wms.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<WmsUser> register(@RequestBody WmsUser user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(user.getRole()==null){
            user.setRole("ROLE_OPERATOR");

        }
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody WmsUser user){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));

        String token =jwtUtils.generateToken(user.getUsername());

        Map<String,String> response=new HashMap<>();
        response.put("token",token);
        return ResponseEntity.ok(response);
    }
}
