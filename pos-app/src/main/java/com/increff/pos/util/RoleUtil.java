package com.increff.pos.util;

import com.increff.pos.model.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RoleUtil {

    public static String getRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication) || Objects.isNull(authentication.getAuthorities())) return "";
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        if (authorities.contains(UserRole.SUPERVISOR.name())) {
            return UserRole.SUPERVISOR.name();
        }
        return UserRole.OPERATOR.name();
    }

}
