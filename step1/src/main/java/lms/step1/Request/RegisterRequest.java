package lms.step1.Request;

import lms.step1.Enumeration.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private Role role;
}