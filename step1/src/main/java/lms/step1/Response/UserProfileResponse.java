package lms.step1.Response;
import lms.step1.Model.User;
import lms.step1.Enumeration.Role;

import lombok.Data;

@Data
public class UserProfileResponse {
    private String firstName;
    private String lastName;
    private String username;
    private Role role;
    private String profilePicture;

    public UserProfileResponse(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.profilePicture = user.getProfilePicture();
    }
}
