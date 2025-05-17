package spring.charityapp.model;

import jakarta.persistence.*;
import lombok.*;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import spring.charityapp.common.ActionState;
import spring.charityapp.common.Category;

import java.util.*;

@Slf4j
@Getter
@Setter // Instead of @Data
@Entity
@Table(name = "charity_action")
@NoArgsConstructor
@AllArgsConstructor
public class CharityAction implements spring.charityapp.blueprints.ICharityAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment in the database
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT") // Store long text
    private String description;

    @Temporal(TemporalType.DATE) // Store only date (no time)
    private Date startDate;

    private String location;

    @Column(name ="objective_amount")
    private Float objectiveAmount;

    // organization id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> medias;

    private Float collectedAmount;


    @Temporal(TemporalType.DATE)
    private Date endDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "charityAction", fetch = FetchType.LAZY)
    private List<Transaction> donations = new ArrayList<>();

    @Enumerated(EnumType.STRING) // Store enum as String
    private Category category;

    @Enumerated(EnumType.STRING)
    private ActionState actionState;

    @Embedded
    private BankInfo bankInfo;

    @Column(name = "created_at")
    private Date createdDate = new Date();

    public void setBankInfo(BankInfo bankInfo) {
        this.bankInfo = bankInfo;
    }
    public BankInfo getBankInfo() {
        return bankInfo;
    }
    public void update(Map<String, String> details) {
        this.title = details.get("title");
        this.description = details.get("description");
        this.location = details.get("location");

        if (Float.parseFloat(details.get("objectiveAmount")) >= 0)
            this.objectiveAmount = Float.parseFloat(details.get("objectiveAmount"));

        this.medias = new HashMap<>();
        for (Map.Entry<String, String> entry : details.entrySet()) {
            if (entry.getKey().contains("media")) {
                this.medias.put(entry.getKey(), entry.getValue());
            }
        }

        this.startDate = new Date(details.get("startDate"));
        this.endDate = new Date(details.get("endDate"));
        this.category = Category.valueOf(details.get("category"));

        log.info("CharityAction has been updated");
    }

    public void archive(ActionState state) {
        this.actionState = state;
        log.info("Action has been archived to: {} " , this.actionState);
    }

    public void consult() {
        log.info(this.toString());
    }

    public void donate(User user, Transaction transaction) {
        this.collectedAmount += transaction.getAmount();
        this.donations.add(transaction); // Add to the list
        log.info("Donation made by: {}  with amount: {} " , user , transaction.getAmount());
    }

    public String getTotalDonations() {
        float total = 0;
        for (Transaction transaction : this.donations) {
            total += transaction.getAmount();
        }
        return "Total donation is " + total;
    }

    @Override
    public String toString() {
        return "CharityAction{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", location='" + location + '\'' +
                ", objectiveAmount=" + objectiveAmount +
                ", organization=" + organization.getName() +
                ", medias=" + medias +
                ", collectedAmount=" + collectedAmount +
                ", endDate=" + endDate +
                ", donations=" + donations +
                ", category=" + category +
                ", actionState=" + actionState +
                ", bankInfo=" + bankInfo +
                ", createdDate=" + createdDate +
                '}';
    }
}

