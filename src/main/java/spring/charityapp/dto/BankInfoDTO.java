package spring.charityapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BankInfoDTO {


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
