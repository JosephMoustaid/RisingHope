package spring.charityapp;

import jakarta.annotation.PostConstruct;
import org.hibernate.validator.spi.messageinterpolation.LocaleResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import spring.charityapp.common.Gender;
import spring.charityapp.common.Status;
import spring.charityapp.model.User;
import spring.charityapp.repository.UserRepository;
import spring.charityapp.service.UserService;
@SpringBootApplication(scanBasePackages ="spring.charityapp")
public class Application {

    private final UserService userService;

    public Application(UserService userService) {
        this.userService = userService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }



    @PostConstruct // Runs on startup
    public void testAddUser() {
        /*
        // Male user from Casablanca
        User user1 = new User();
        user1.setEmail("karim.elami@gmail.com");
        user1.setPassword("Pass1234!");
        user1.setFirstname("Karim");
        user1.setLastname("El Ami");
        user1.setGender(Gender.MALE);
        user1.setStatus(Status.ACTIVE);
        userService.addUser(user1);

        // Female user from Rabat
        User user2 = new User();
        user2.setEmail("fatimazahra.benali@outlook.com");
        user2.setPassword("Fz@2023");
        user2.setFirstname("Fatima Zahra");
        user2.setLastname("Ben Ali");
        user2.setGender(Gender.FEMALE);
        user2.setStatus(Status.ACTIVE);
        userService.addUser(user2);

        // Male user from Marrakech
        User user3 = new User();
        user3.setEmail("youssef_alami@yahoo.com");
        user3.setPassword("Marrakech2024");
        user3.setFirstname("Youssef");
        user3.setLastname("Alami");
        user3.setGender(Gender.MALE);
        user3.setStatus(Status.ACTIVE);
        userService.addUser(user3);

        // Female user from Tangier
        User user4 = new User();
        user4.setEmail("sara.tanger@live.com");
        user4.setPassword("SaraTanger01!");
        user4.setFirstname("Sara");
        user4.setLastname("Bennis");
        user4.setGender(Gender.FEMALE);
        user4.setStatus(Status.PENDING);
        userService.addUser(user4);

        // Male user from Fes (admin)
        User user5 = new User();
        user5.setEmail("admin@fescharity.org");
        user5.setPassword("AdminFes123!");
        user5.setFirstname("Mehdi");
        user5.setLastname("El Fassi");
        user5.setGender(Gender.MALE);
        user5.setStatus(Status.ACTIVE);
        userService.addUser(user5);

         */
    }

}

