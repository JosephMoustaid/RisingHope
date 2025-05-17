package spring.charityapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;
import spring.charityapp.common.Gender;
import spring.charityapp.common.Status;
import spring.charityapp.service.Bcrypt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;
@Slf4j
@Data
@Entity
@Table(name = "\"user\"")  // Notice the escaped quotes
@NoArgsConstructor
public class User implements spring.charityapp.blueprints.IUser, spring.charityapp.blueprints.IAuth {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment in the database
    private Integer id;

    @Column(nullable = false)
    private String firstname;
    @Column(nullable = false)
    private String lastname;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private String email;

    @Setter
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    // we have the hashed password here
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Date joinedDate = new Date();

    private String profile;

    // == All args constructor ==
    public User( String firstname , String lastname,
                Gender gender, String email,
                String password, Status status , String profile) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.status = status;
        this.email = email;
        setPassword(password);
        this.profile = profile;
    }


    public void modifierProfile(Map<String, String> details) {
        this.firstname = details.get("firstname");
        this.lastname = details.get("lastname");
        this.gender = Gender.valueOf(details.get("gender"));
        // here we use the crypted password
        this.password = Bcrypt.hashPassword(details.get("password"));
        this.status = Status.valueOf(details.get("status"));
    }

    public void setPassword(String password){
        this.password = Bcrypt.hashPassword(password);
        log.info("Password has been set");
    }
    public boolean checkPassword(String plainPassword){
        return Bcrypt.checkPassword(plainPassword , this.password);
    }
    public void consultHistory() {
        // TODO implement here
    }
    public void deleteAccount() {
        // TODO implement here
    }
    public boolean login(String email, String password) {
        return this.email.equals(email) && checkPassword(password);
    }
    public void register(Map<String, String> details) {
        this.id = Integer.parseInt(details.get("id"));
        this.firstname = details.get("firstname");
        this.lastname = details.get("lastname");
        this.gender = Gender.valueOf(details.get("gender"));
        this.email = details.get("email");
        // we use the crypted password
        setPassword(details.get("password"));
        this.status = Status.ACTIVE ;
        log.info("User has been registered succesfully");
    }
    public void logout() {
        // TODO implement her
    }
    public void resetPassword(String email) {
        // TODO implement here
    }
    public void addToHistory(CharityAction charityAction) {

    }
    public void removeFromHistory(CharityAction charityAction) {

    }
}