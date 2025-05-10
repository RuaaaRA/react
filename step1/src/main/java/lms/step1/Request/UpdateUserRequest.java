package lms.step1.Request;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String password; // Optional
    private String profilePicture; // Optional, null means remove profile picture
}
