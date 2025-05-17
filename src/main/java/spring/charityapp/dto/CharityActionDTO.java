package spring.charityapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import spring.charityapp.model.BankInfo;

import java.util.ArrayList;
@AllArgsConstructor
@NoArgsConstructor
public class CharityActionDTO {
    private int id;
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;


    // media should have at least one element
    @NotNull(message = "Media cannot be null")
    @NotBlank(message = "Media cannot be blank")
    @Size(min = 1, message = "Media should have at least one element")
    private List<String> media= new ArrayList<>();
    @NotNull(message = "Objective Amount cannot be null")
    private Float objectiveAmount;

    private Float collectedAmount;

    @NotNull(message = "Start Date cannot be blank")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @NotNull(message = "End Date cannot be blank")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    @NotBlank(message = "Location cannot be blank")
    private String location;
    @NotBlank(message = "Currency cannot be blank")
    private String currency;
    private List<TransactionDTO> donations = new ArrayList<>();
    @NotBlank(message = "Category cannot be blank")
    private String category;
    @NotBlank(message = "Action State cannot be blank")
    private String actionState;


    @NotNull(message = "Bank Info cannot be null")
    private BankInfo bankInfo;

    // dont include it in the constructor

    private Date createdAt;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getMedia() {
        return media;
    }

    public void setMedia(List<String> media) {
        this.media = media;
    }

    public Float getObjectiveAmount() {
        return objectiveAmount;
    }

    public void setObjectiveAmount(Float total) {
        this.objectiveAmount = total;
    }

    public Float getCollectedAmount() {
        return collectedAmount;
    }

    public void setCollectedAmount(Float collectedAmount) {
        this.collectedAmount = collectedAmount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<TransactionDTO> getDonations() {
        return donations;
    }

    public void setDonations(List<TransactionDTO> donations) {
        this.donations = donations;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getActionState() {
        return actionState;
    }

    public void setActionState(String actionState) {
        this.actionState = actionState;
    }

    public BankInfo getBankInfo() {
        return bankInfo;
    }
    public void setBankInfo(BankInfo bankInfo) {
        this.bankInfo = bankInfo;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String toString(){
        return "id :" + id + "\n" +
                "title :" + title + "\n" +
                "description :" + description + "\n" +
                "objectiveAmount :" + objectiveAmount + "\n" +
                "collectedAmount :" + collectedAmount + "\n" +
                "startDate :" + startDate + "\n" +
                "endDate :" + endDate + "\n" +
                "location :" + location + "\n" +
                "currency :" + currency + "\n" +
                "category :" + category + "\n" +
                "actionState :" + actionState + "\n" +
                "bankInfo :" + bankInfo + "\n" +
                "createdAt :" + createdAt;
    }

    // constructor with`out id
    public CharityActionDTO(int id ,String title, String description, List<String> media, Float objectiveAmount, Float collectedAmount, Date startDate, Date endDate, String location, String currency, List<TransactionDTO> donations, String category, String actionState, BankInfo bankInfo) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.media = media;
        this.objectiveAmount = objectiveAmount;
        this.collectedAmount = collectedAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.currency = currency;
        this.donations = donations;
        this.category = category;
        this.actionState = actionState;
        this.bankInfo = bankInfo;
    }
}