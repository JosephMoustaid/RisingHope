package spring.charityapp.dto;

import spring.charityapp.common.ActionState;
import spring.charityapp.common.PaymentMethod;

import java.time.LocalTime;
import java.util.Date;

public class TransactionDTO {
    private Integer id;
    private Date date;
    private LocalTime time;
    private Integer charityActionId;
    private Integer userId;
    private Float amount;
    private PaymentMethod paymentMethod;
    private ActionState state;
    private String referenceNumber;

    // Default constructor
    public TransactionDTO() {}

    // All-args constructor
    public TransactionDTO(Integer id, Date date, LocalTime time,
                          Integer charityActionId, Integer userId, Float amount,
                          PaymentMethod paymentMethod, ActionState state,
                          String referenceNumber) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.charityActionId = charityActionId;
        this.userId = userId;
        setAmount(amount);
        this.paymentMethod = paymentMethod;
        this.state = state;
        setReferenceNumber(referenceNumber);
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }




    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Integer getCharityActionId() {
        return charityActionId;
    }

    public void setCharityActionId(Integer charityActionId) {
        this.charityActionId = charityActionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public ActionState getState() {
        return state;
    }

    public void setState(ActionState state) {
        this.state = state;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        if (referenceNumber == null || referenceNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Reference number is required");
        }
        this.referenceNumber = referenceNumber;
    }
}