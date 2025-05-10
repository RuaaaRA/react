package lms.step1.Repository;

import lms.step1.Model.User;
import lms.step1.Enumeration.Role;  // Make sure to import your Role enum
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
    // Add this method to find users by role
    List<User> findByRole(Role role);
}