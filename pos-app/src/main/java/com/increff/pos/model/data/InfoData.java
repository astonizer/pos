package com.increff.pos.model.data;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class InfoData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String message;
    private String email;
    private String role;
    private Boolean shown;

    public InfoData() {
        message = "No message";
        email = "No email";
        role = "No role";
        shown = false;
    }

}