package spring.charityapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@Validated
public class OrganizationDTO {

    private Integer id;

    @NotBlank(message = "Name cannot be blank")
    @Size(min= 5 , max = 50, message = "Name cannot exceed 50 characters")
    private String name;

    @NotBlank(message = "Legal Address cannot be blank")
    @Size(max = 255, message = "Legal Address cannot exceed 255 characters")
    private String legalAddress;

    @NotBlank(message = "Phone Number cannot be blank")
    @Size(max = 15, message = "Phone Number cannot exceed 15 characters")
    private String phoneNumber;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;


    private Map<String, String> media;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Confirm Password cannot be blank")
    private String confirmPassword;

    private String status;
    private Boolean validated;
    private String profileBanner;

    @NotBlank(message = "HTML cannot be blank")
    private String htmlContent;


    private List<MultipartFile> images;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLegalAddress() {
        return legalAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, String> getMedia() {
        return media;
    }

    public Boolean getValidated() {
        return validated;
    }

    public String getProfileBanner() {
        return profileBanner;
    }

    public List<MultipartFile> getImages() {
        return images;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setLegalAddress(String legalAddress) {
        this.legalAddress = legalAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setMedia(Map<String, String> media) {
        this.media = media;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public void setProfileBanner(String profileBanner) {
        this.profileBanner = profileBanner;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }

    public OrganizationDTO(Integer id, String name, String legalAddress, String phoneNumber, String email, Map<String, String> media, String confirmPassword, String status, String htmlContent, Boolean validated, String profileBanner, String password, List<MultipartFile> images) {
        this.id = id;
        this.name = name;
        this.legalAddress = legalAddress;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.media = media;
        this.confirmPassword = confirmPassword;
        this.status = status;
        this.htmlContent = htmlContent;
        this.validated = validated;
        this.profileBanner = profileBanner;
        this.password = password;
        this.images = images;
    }

    public OrganizationDTO() {

    }

    @Override
    public String toString() {
        return "OrganizationDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", legalAddress='" + legalAddress + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", media=" + media +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                ", status='" + status + '\'' +
                ", validated=" + validated +
                ", profileBanner='" + profileBanner + '\'' +
                ", htmlContent='" + htmlContent + '\'' +
                ", images=" + images +
                '}';
    }
}