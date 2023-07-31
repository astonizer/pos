package com.increff.pos.util;

import com.increff.pos.pojo.UserPojo;
import com.increff.pos.model.enums.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class AuthenticationUtil {

    private static List<String> supervisorList;
    
    @Value("${supervisors}")
    private void setSupervisorList(String supervisors) {
        supervisorList = Arrays.asList(supervisors.split(","));
    }

    public static UserRole assignRoleByEmail(String email) {
        if(checkSupervisor(email)) {
            return UserRole.SUPERVISOR;
        }
        return UserRole.OPERATOR;
    }

    public static UserRole getUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication) || Objects.isNull(authentication.getAuthorities())) return null;
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        if (authorities.contains(UserRole.SUPERVISOR.name())) {
            return UserRole.SUPERVISOR;
        }
        return UserRole.OPERATOR;
    }
    
    public static Authentication convert(UserPojo userPojo) {
        // Create principal
        UserPrincipal principal = new UserPrincipal();
        principal.setEmail(userPojo.getEmail());
        principal.setId(userPojo.getId());

        // Create Authorities
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userPojo.getRole().name()));

        // Create Authentication
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    private static Boolean checkSupervisor(String email) {
        return supervisorList.contains(email);
    }

}
