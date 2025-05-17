package spring.charityapp.util;


import lombok.extern.slf4j.Slf4j;
import spring.charityapp.common.Category;
import spring.charityapp.common.Status;
import spring.charityapp.dto.*;
import spring.charityapp.model.CharityAction;
import spring.charityapp.model.Organization;
import spring.charityapp.model.Transaction;
import spring.charityapp.model.User;
import spring.charityapp.service.Bcrypt;

;import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// map DTOs to entities and vice versa
@Slf4j
public class Mapper {
    public Mapper() {}

    public UserDTO mapUserToUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getGender(),
                user.getEmail(),
                user.getPassword(),
                user.getPassword(),
                user.getStatus() ,
                user.getProfile(),
                user.getTransactions().stream()
                                .map(transaction -> {
                                    return mapTransactionToTransactionDTO(transaction);
                                }).collect(Collectors.toList()) ,
                user.getJoinedDate()
                );
    }

    public User mapUserDTOToUser(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setFirstname(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setGender(userDTO.getGender());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setStatus(userDTO.getStatus());
        user.setProfile(userDTO.getProfile());
        user.setJoinedDate(userDTO.getJoinedDate());
        for(TransactionDTO transactionDTO : userDTO.getTransactions()) {
            user.getTransactions().add(mapTransactionDTOToTransaction(transactionDTO));
        }
        return user;
    }

    public TransactionDTO mapTransactionToTransactionDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getDate(),
                transaction.getTime(),
                transaction.getCharityAction().getId(),
                transaction.getUser().getId(),
                transaction.getAmount(),
                transaction.getPaymentMethod(),
                transaction.getState(),
                transaction.getReferenceNumber()
        );
    }
    public Transaction mapTransactionDTOToTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setId(transactionDTO.getId());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setDate(transactionDTO.getDate());
        transaction.setTime(transactionDTO.getTime());
        transaction.setCharityAction(null); // Set this later
        transaction.setUser(null); // Set this later
        transaction.setPaymentMethod(transactionDTO.getPaymentMethod());
        transaction.setState(transactionDTO.getState());
        transaction.setReferenceNumber(transactionDTO.getReferenceNumber());

        return transaction;
    }
    public User mapUserRegisterDTOToUser(UserRegisterDTO userRegisterDTO) {
        User user = new User();
        user.setFirstname(userRegisterDTO.getFirstname());
        user.setLastname(userRegisterDTO.getLastname());
        user.setGender(userRegisterDTO.getGender());
        user.setEmail(userRegisterDTO.getEmail());

        user.setPassword(userRegisterDTO.getPassword());
        // check the assigned password
        log.info("the password that has been asigned : " + user.getPassword());
        // does it match the password again
        log.info("Does it match the password again : " + Bcrypt.checkPassword(userRegisterDTO.getPassword(), user.getPassword()) );
        user.setStatus(Status.ACTIVE);

        return user;
    }

    public OrganizationDTO mapOrganizationToOrganiationDTO(Organization organization) {

        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setId(organization.getId());
        organizationDTO.setName(organization.getName());
        organizationDTO.setLegalAddress(organization.getLegalAddress());
        organizationDTO.setPhoneNumber(organization.getPhoneNumber());
        organizationDTO.setEmail(organization.getEmail());
        organizationDTO.setMedia(organization.getMedia());
        organizationDTO.setStatus(organization.getStatus().toString()); // Enum to String
        organizationDTO.setValidated(organization.getValidated());
        organizationDTO.setProfileBanner(organization.getMedia().get("image_0"));
        organizationDTO.setHtmlContent(organization.getHtmlContent());
        return organizationDTO;
    }

    public Organization mapOrganizationDtoToOrganization(OrganizationDTO org){
        Organization organization = new Organization();
        organization.setName(org.getName());
        organization.setLegalAddress(org.getLegalAddress());
        organization.setPhoneNumber(org.getPhoneNumber());
        organization.setEmail(org.getEmail());
        organization.setPassword(org.getPassword());
        organization.setProfileBanner(org.getProfileBanner());
        organization.setHtmlContent(org.getHtmlContent());
        organization.setValidated(false);
        organization.setMedia(org.getMedia());
        organization.setStatus(Status.PENDING);
        organization.setProfileBanner(org.getMedia().get("image_0"));
        return organization;
    }



    public CharityActionDTO mapCharityActionToCharityActionDTO(CharityAction action) {
        List<String> mediaUrls = action.getMedias() != null
                ? action.getMedias().values().stream().toList()
                : List.of();

        List<TransactionDTO> donationsDTO = action.getDonations() != null
                ? action.getDonations().stream().map(
                        donation -> mapTransactionToTransactionDTO(donation)
        ).collect(Collectors.toList())
                : List.of();

        return new CharityActionDTO(
                action.getId(),
                action.getTitle(),
                action.getDescription(),
                mediaUrls,
                action.getObjectiveAmount(),
                action.getCollectedAmount(),
                action.getStartDate(),
                action.getEndDate(),
                action.getLocation(),
                "USD", // You can make this dynamic if needed
                donationsDTO,
                action.getCategory().toString(),
                action.getActionState().toString(),
                action.getBankInfo()
        );
    }

    public CharityAction mapCharityActionDTOToCharityAction(CharityActionDTO charityActionDTO) {
        CharityAction charityAction = new CharityAction();
        charityAction.setId(charityActionDTO.getId());
        charityAction.setTitle(charityActionDTO.getTitle());
        charityAction.setDescription(charityActionDTO.getDescription());
        charityAction.setObjectiveAmount(charityActionDTO.getObjectiveAmount());
        charityAction.setCollectedAmount(charityActionDTO.getCollectedAmount());
        charityAction.setStartDate(charityActionDTO.getStartDate());
        charityAction.setEndDate(charityActionDTO.getEndDate());
        charityAction.setLocation(charityActionDTO.getLocation());
        charityAction.setCategory(charityAction.getCategory());
        charityAction.setBankInfo(charityActionDTO.getBankInfo());
        Map<String, String> medias = new HashMap<>();
        for (int i = 0; i < charityActionDTO.getMedia().size(); i++) {
            medias.put("image_" + i, charityActionDTO.getMedia().get(i));
        }
        charityAction.setMedias(medias);

        // now transactions
        List<Transaction> doantions = charityActionDTO.getDonations()
                .stream()
                .map(transactionDTO -> mapTransactionDTOToTransaction(transactionDTO))
                .collect(Collectors.toList());
        charityAction.setDonations(doantions);

        return charityAction;
    }


}
