package spring.charityapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;
import spring.charityapp.common.ActionState;
import spring.charityapp.common.PaymentMethod;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Getter @Setter
@Entity
@Table(name = "transaction")
@NoArgsConstructor
@Slf4j
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment in the database
    private Integer id;

    @Column(nullable = false)
    private Float amount;


    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date date;

    @Temporal(TemporalType.TIME)
    @Column(nullable = false)
    private LocalTime time; // Using LocalTime instead of java.sql.Time


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charity_action_id")
    private CharityAction charityAction;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;




    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ActionState state;

    @Column(nullable = false)
    private String referenceNumber;

    public void update(Map<String, String> details) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            this.date = sdf.parse(details.get("date"));

            this.time = LocalTime.parse(details.get("time")); // Using LocalTime.parse() instead of Time constructor

            this.referenceNumber = details.get("referenceNumber");
            this.amount = Float.parseFloat(details.get("amount")); // Fixed typo here

            if (this.charityAction == null) {
                throw new IllegalStateException("CharityAction is required");
            }
            log.info("Transaction number {} * updated", this.id);

        } catch (ParseException | NumberFormatException | DateTimeParseException e) {
            e.printStackTrace();
        }
    }

    /*
    public void setUser(User user) {
        this.user = user;
        if (user != null && !user.getTransactions().contains(this)) {
            user.getTransactions().add(this);
        }
    }

    public void setCharityAction(CharityAction charityAction) {
        this.charityAction = charityAction;
        if (charityAction != null && !charityAction.getDonations().contains(this)) {
            charityAction.getDonations().add(this);
        }
    } */
}
