package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class UserLoginForm {


    @NotEmpty(message = "Empty email")
    @Email(message = "Invalid email")
    private String email;

    @NotEmpty(message = "Empty password")
    private String password;

}
