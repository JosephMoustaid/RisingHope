package spring.charityapp.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import spring.charityapp.service.Bcrypt;

import java.util.*;

@Slf4j
@Getter
@Setter // Instead of @Data
@Entity
@Table(name = "super_admin")
@NoArgsConstructor
public class SuperAdmin implements spring.charityapp.blueprints.ISuperAdmin, spring.charityapp.blueprints.IAuth {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment in the database
    private Integer id;

    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;


    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<String> organizationsWaitingList = new ArrayList<String>();


    public SuperAdmin(String email, String password) {
        this.email = email;
        setPassword(password);
        this.organizationsWaitingList = new ArrayList<String>();
    }

    public void setPassword(String password) {
        this.password = Bcrypt.hashPassword(password);
    }

    public boolean checkPassword(String plainPassword) {
        return Bcrypt.checkPassword(plainPassword, this.password);
    }

    public boolean login(String email, String password) {
        return this.email.equals(email) && checkPassword(password);
    }

    @Override
    public void register(Map<String, String> details) {
        this.email = details.get("email");
        setPassword(details.get("password"));
    }

    public void logout() {
        // TODO implement here
    }

    public void resetPassword(String password) {
        if(password != null && !password.isEmpty() && password.length() > 10) {
            setPassword(password);
            log.info("SuperAdmin password has been reset");
        }
    }

    @Override
    public void validateOrganisation(String id) {
        /*
        if(organizationsWaitingList != null) {
            for (Organization organization : organizationsWaitingList) {
                if (organization.getId().equals(id)) {
                    organization.setStatus(spring.charityapp.common.Status.ACTIVE);
                    log.info("Organization " + organization.getName() + " has been validated by SuperAdmin");
                    organizationsWaitingList.remove(organization);
                    break;
                }
            }
        } */
    }
}