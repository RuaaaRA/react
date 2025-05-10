package lms.step1.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class WelcomeController {

    @GetMapping("/welcome")
    public String welcome() {
        log.info("🎉 /welcome endpoint accessed after successful Google login");
        return "Welcome! Google login successful 🎉";
    }
}
