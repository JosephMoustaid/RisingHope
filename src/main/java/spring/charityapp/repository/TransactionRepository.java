package spring.charityapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.charityapp.model.Transaction;
import spring.charityapp.common.ActionState;
import spring.charityapp.common.PaymentMethod;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    // Find all by user ID
    List<Transaction> findByUserId(Integer userId);

    // Find all by Charity Action ID
    List<Transaction> findByCharityActionId(Integer charityActionId);

    // Find all by date
    List<Transaction> findByDate(Date date);

    // Find all by payment method
    List<Transaction> findByPaymentMethod(PaymentMethod paymentMethod);

    // Find all by state
    List<Transaction> findByState(ActionState state);

    // Find by reference number
    Transaction findByReferenceNumber(String referenceNumber);

    // Optional: Top donations for a specific charity action
    List<Transaction> findTop5ByCharityActionIdOrderByAmountDesc(Integer charityActionId);
}
