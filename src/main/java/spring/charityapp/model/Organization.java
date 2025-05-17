package spring.charityapp.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jnr.ffi.annotations.In;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import spring.charityapp.common.Status;

import java.util.*;
import spring.charityapp.service.Bcrypt;
@Getter
@Setter // Instead of @Data
@Entity
@Table(name = "organization")
public class Organization implements spring.charityapp.blueprints.IOrganization, spring.charityapp.blueprints.IAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment in the database
    private Integer id;
    @Column(nullable = false)
    private String name;
    private String legalAddress;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private String email;


    @Column(columnDefinition = "TEXT") // Use PostgreSQL's TEXT type
    private String htmlContent; // This holds the large HTML description

    // we have the hashed password here
    @Column(nullable = false)
    private String password;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> media;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Boolean validated;

    private String profileBanner;

    private Date joinedDate = new Date();

    public Organization(String name, String legalAddress, String phoneNumber, String email, String password, String profileBanner) {
        this.name = name;
        this.legalAddress = legalAddress;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = Bcrypt.hashPassword(password);
        this.status = Status.PENDING;
        this.validated = false;
        this.profileBanner = profileBanner;
    }
    public Organization() {}
    public void setPassword(String password){
        this.password = Bcrypt.hashPassword(password);
    }
    public boolean checkPassword(String plainPassword){
        return Bcrypt.checkPassword(plainPassword , this.password);
    }
    public void manageProfile() {
    }
    public void approveOrganization(String organizationId) {
    }
    public void addAction(CharityAction action) {
    }
    public boolean login(String email, String password) {
        return this.email.equals(email) && checkPassword(password);
    }
    public void register(Map<String, String> details) {
    }
    public void logout() {
    }
    public void resetPassword(String email) {
    }
}
