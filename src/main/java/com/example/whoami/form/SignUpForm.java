package com.example.whoami.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpForm {
    private String id;
    private String password;
    private String passwordCheck;
    private String email;
}
