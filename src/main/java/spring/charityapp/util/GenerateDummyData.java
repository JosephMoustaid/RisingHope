package spring.charityapp.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.charityapp.common.*;
import spring.charityapp.model.*;
import spring.charityapp.repository.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class GenerateDummyData {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final CharityActionRepository charityActionRepository;
    private final TransactionRepository transactionRepository;

    private final Random random = new Random();

    private static final String[] FIRST_NAMES = {
            "Adam", "Lina", "Omar", "Yasmine", "Anas", "Nada", "Walid", "Aya", "Ismail", "Sara",
            "Soufiane", "Malak", "Youssef", "Nora", "Reda", "Khadija", "Zakaria", "Salma", "Ilham", "Hicham"
    };

    private static final String[] LAST_NAMES = {
            "Alaoui", "El Amrani", "Benchekroun", "Skalli", "El Idrissi", "Tahiri", "Rhani", "Fassi", "Bakkali", "Ouazzani"
    };

    private static final String[] SECTORS = {"Health", "Education", "Environment", "Social"};
    private static final String[] CITIES = {"Casablanca", "Rabat", "Marrakech", "Fes", "Agadir", "Tangier", "Meknes", "Oujda"};
    private static final String[] STREETS = {"Rue Atlas", "Ave. Mohammed V", "Bd. Anfa", "Rue Ibn Sina", "Rue des Palmiers"};
    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            /*
            generateUsers();
            generateOrganizationsAndActions();

             */
        }
    }

    private void generateUsers() {
        List<User> users = new ArrayList<>();

        for (int i = 0; i < 150; i++) {
            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + i + "@example.org";

            users.add(new User(
                    firstName,
                    lastName,
                    i % 2 == 0 ? Gender.MALE : Gender.FEMALE,
                    email,
                    "securePass123",
                    Status.ACTIVE,
                    "avatar" + (i % 10 + 1) + ".jpg"
            ));
        }

        userRepository.saveAll(users);
    }

    private void generateOrganizationsAndActions() {
        List<Organization> orgs = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            String name = "Org " + (char) ('A' + i);
            String sector = SECTORS[i % SECTORS.length];
            String phone = "06" + (10000000 + random.nextInt(89999999));
            String email = name.toLowerCase().replace(" ", "") + "@charity.org";
            String address = (random.nextInt(300) + 1) + " " + STREETS[random.nextInt(STREETS.length)] + ", " + CITIES[random.nextInt(CITIES.length)];

            Organization org = new Organization(name, address, phone, email, "pass" + i, "banner" + (i % 5 + 1) + ".jpg");
            org.setStatus(Status.ACTIVE);
            org.setValidated(true);

            orgs.add(org);
        }

        organizationRepository.saveAll(orgs);

        for (Organization org : orgs) {
            int actions = 5 + random.nextInt(6); // 5–10 actions
            for (int i = 1; i <= actions; i++) {
                CharityAction action = createAction(org, i);
                charityActionRepository.save(action);
                generateDonations(action);
            }
        }
    }

    private CharityAction createAction(Organization org, int index) {
        CharityAction action = new CharityAction();
        action.setTitle(org.getName() + " - Action " + index);
        action.setDescription("This is a campaign for " + org.getName() + " to support the org's needs.");
        action.setLocation(CITIES[random.nextInt(CITIES.length)]);
        float total = 20000f + random.nextInt(80000);
        action.setObjectiveAmount(total);
        action.setCollectedAmount(0f);

        action.setCategory(Category.values()[random.nextInt(Category.values().length)]);
        action.setActionState(ActionState.values()[random.nextInt(ActionState.values().length)]);

        LocalDate start = LocalDate.now().minusDays(random.nextInt(180));
        LocalDate end = start.plusDays(30 + random.nextInt(90));
        action.setStartDate(Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        action.setEndDate(Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        Map<String, String> media = new HashMap<>();
        media.put("main_image", "action_" + org.getName().replace(" ", "_") + "_" + index + ".jpg");
        media.put("video", "vid_" + random.nextInt(1000) + ".mp4");
        media.put("doc", "desc_" + random.nextInt(1000) + ".pdf");
        action.setMedias(media);

        return action;
    }

    private void generateDonations(CharityAction action) {
        List<User> users = userRepository.findAll();
        int count = 30 + random.nextInt(91); // 30–120 donations

        for (int i = 0; i < count; i++) {
            User user = users.get(random.nextInt(users.size()));

            Transaction tx = new Transaction();
            float amount = 20f + random.nextFloat() * 980f;
            tx.setAmount(amount);
            tx.setCharityAction(action);
            tx.setUser(user);
            tx.setDate(randomDateBetween(action.getStartDate(), action.getEndDate()));
            tx.setTime(LocalTime.of(random.nextInt(24), random.nextInt(60)));
            tx.setPaymentMethod(PaymentMethod.values()[random.nextInt(PaymentMethod.values().length)]);
            tx.setState(ActionState.values()[random.nextInt(ActionState.values().length)]);
            tx.setReferenceNumber(UUID.randomUUID().toString());

            transactionRepository.save(tx);

            action.setCollectedAmount(action.getCollectedAmount() + amount);
            action.getDonations().add(tx);
        }

        charityActionRepository.save(action);
    }

    private Date randomDateBetween(Date start, Date end) {
        long startMillis = start.getTime();
        long endMillis = end.getTime();
        return new Date(ThreadLocalRandom.current().nextLong(startMillis, endMillis + 1));
    }
}
