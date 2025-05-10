package lms.step1.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageDTO {

    @NotBlank(message = "Message must not be blank")
    @Size(min = 1, max = 500, message = "Message must be between 1 and 500 characters")
    private String message;
}
