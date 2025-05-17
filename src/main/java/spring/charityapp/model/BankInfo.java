package spring.charityapp.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankInfo {

    @NotBlank(message = "Bank name is required")
    @Size(max = 100, message = "Bank name must be less than 100 characters")
    private String bankName;

    @NotBlank(message = "Account holder name is required")
    @Size(max = 100, message = "Account holder name must be less than 100 characters")
    private String accountHolderName;

    @NotBlank(message = "IBAN is required")
    @Pattern(regexp = "^[A-Z]{2}\\d{2}[A-Z0-9]{11,30}$", message = "Invalid IBAN format")
    private String iban;

    @NotBlank(message = "SWIFT code is required")
    @Pattern(regexp = "^[A-Z]{6}[A-Z0-9]{2,5}$", message = "Invalid SWIFT code format")
    private String swiftCode;

    @Override
    public String toString() {
        return accountHolderName + " (" + bankName + ")";
    }
}
