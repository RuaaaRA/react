package lms.step1.Request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}

