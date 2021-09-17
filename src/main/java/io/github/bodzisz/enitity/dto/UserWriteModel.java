package io.github.bodzisz.enitity.dto;

import javax.validation.constraints.NotBlank;

public class UserWriteModel {

    private String name;
    @NotBlank(message = "username can not be empty")
    private String username;
    @NotBlank(message = "password can not be empty")
    private String password;
    @NotBlank(message = "password confirmation can not be empty")
    private String confirmedPassword;

    public UserWriteModel() {
    }

    public UserWriteModel(String name, String username, String password, String confirmedPassword) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.confirmedPassword = confirmedPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmedPassword() {
        return confirmedPassword;
    }

    public void setConfirmedPassword(String confirmedPassword) {
        this.confirmedPassword = confirmedPassword;
    }
}
