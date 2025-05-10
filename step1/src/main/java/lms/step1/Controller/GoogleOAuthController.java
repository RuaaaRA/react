package lms.step1.Controller;

import lms.step1.Response.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class GoogleOAuthController {

    @GetMapping("/auth/google-link")
    public EntityModel<MessageResponse> getGoogleLoginLink() {
        log.info("[GET] /auth/google-link - Generating Google OAuth2 login link");

        MessageResponse message = new MessageResponse("Click the link to login with Google");

        Link link = Link.of("http://localhost:8080/oauth2/authorization/google?redirect_uri=http://localhost:3000/oauth2/redirect")
                       .withRel("google-login");

        return EntityModel.of(message, link);
    }
}