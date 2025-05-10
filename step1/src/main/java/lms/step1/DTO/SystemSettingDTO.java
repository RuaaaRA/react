package lms.step1.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SystemSettingDTO {

    @NotBlank(message = "Key must not be blank")
    private String key;

    @NotBlank(message = "Value must not be blank")
    private String value;
}
