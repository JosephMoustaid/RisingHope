package spring.charityapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.charityapp.dto.TransactionDTO;
import spring.charityapp.model.CharityAction;
import spring.charityapp.model.Transaction;
import spring.charityapp.model.User;
import spring.charityapp.repository.CharityActionRepository;
import spring.charityapp.repository.TransactionRepository;
import spring.charityapp.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepo;
    private final UserRepository userRepo;
    private final CharityActionRepository charityActionRepo;

    // Convert entity to DTO
    private TransactionDTO toDTO(Transaction tx) {
        return new TransactionDTO(
                tx.getId(),
                tx.getDate(),
                tx.getTime(),
                tx.getCharityAction() != null ? tx.getCharityAction().getId() : null,
                tx.getUser() != null ? tx.getUser().getId() : null,
                tx.getAmount(),
                tx.getPaymentMethod(),
                tx.getState(),
                tx.getReferenceNumber()
        );
    }

    // Convert DTO to entity
    private Transaction fromDTO(TransactionDTO dto) {
        Transaction tx = new Transaction();
        tx.setId(dto.getId());
        tx.setDate(dto.getDate());
        tx.setTime(dto.getTime());
        tx.setAmount(dto.getAmount());
        tx.setPaymentMethod(dto.getPaymentMethod());
        tx.setState(dto.getState());
        tx.setReferenceNumber(dto.getReferenceNumber());

        if (dto.getUserId() != null) {
            User user = userRepo.findById(dto.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
            tx.setUser(user);
        }

        if (dto.getCharityActionId() != null) {
            CharityAction ca = charityActionRepo.findById(dto.getCharityActionId()).orElseThrow(() -> new IllegalArgumentException("CharityAction not found"));
            tx.setCharityAction(ca);
        }

        return tx;
    }

    // Save new transaction
    public TransactionDTO createTransaction(TransactionDTO dto) {
        Transaction tx = fromDTO(dto);
        return toDTO(transactionRepo.save(tx));
    }

    // Get all transactions
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepo.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Get transaction by ID
    public Optional<TransactionDTO> getTransactionById(Integer id) {
        return transactionRepo.findById(id).map(this::toDTO);
    }

    // Delete by ID
    public boolean deleteTransaction(Integer id) {
        if (transactionRepo.existsById(id)) {
            transactionRepo.deleteById(id);
            return true;
        }
        return false;
    }

    // Get top 5 transactions for a charity action
    public List<TransactionDTO> getTop5DonationsByCharityActionId(Integer charityActionId) {
        return transactionRepo.findTop5ByCharityActionIdOrderByAmountDesc(charityActionId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Get all transactions by user
    public List<TransactionDTO> getTransactionsByUser(Integer userId) {
        return transactionRepo.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
