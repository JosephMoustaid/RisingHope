package spring.charityapp.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import spring.charityapp.common.Role;
import spring.charityapp.common.Status;
import spring.charityapp.model.User;
import spring.charityapp.service.AdminService;
import spring.charityapp.service.AuthService;
import spring.charityapp.service.OrganizationService;
import spring.charityapp.service.UserService;
import spring.charityapp.util.JwtUtil;
import spring.charityapp.util.Mapper;
import org.springframework.ui.Model;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("")
public class OauthController {
    private final AuthService authService;
    private final AdminService superadminService;
    private final OrganizationService organizationService;


    private final UserService userService;
    private Mapper mapper = new Mapper();
    private final String uploadDir = "/#  remove for securituy reasons";



    private final String secret = "#  remove for securituy reasons";

    private final String redirectUri = "#  remove for securituy reasons";
    private final String clientId = "#  remove for securituy reasons";
    private final String scope = "#  remove for securituy reasons";


    public OauthController(AuthService authService, OrganizationService organizationService, UserService userService, AdminService adminService) {
        this.authService = authService;
        this.superadminService = adminService;
        this.userService = userService;
        this.organizationService = organizationService;
    }


    @GetMapping("/auth/oauth2/google")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        String authUrl = "#  remove for securituy reasons"
                + "?client_id=" + clientId
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&response_type=code"
                + "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8)
                + "&access_type=offline"
                + "&prompt=select_account";

        response.sendRedirect(authUrl);
    }
    @GetMapping("/login/oauth2/code/google")
    public String handleGoogleCallback(
            @RequestParam("code") String code,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            // Step 1: Token exchange
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", clientId);
            params.add("client_secret", secret);
            params.add("redirect_uri", redirectUri);
            params.add("grant_type", "authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            Map<String, Object> tokenResponse = restTemplate.postForObject(
                    "#  remove for securituy reasons",
                    request,
                    Map.class
            );

            String accessToken = (String) tokenResponse.get("access_token");
            log.info("Access token by Google: {}", accessToken);

            // Step 2: Get user info
            HttpHeaders userHeaders = new HttpHeaders();
            userHeaders.setBearerAuth(accessToken);
            HttpEntity<String> userRequest = new HttpEntity<>(userHeaders);

            ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                    "#  remove for securituy reasons",
                    HttpMethod.GET,
                    userRequest,
                    Map.class
            );

            Map<String, Object> userInfo = userInfoResponse.getBody();
            String email = (String) userInfo.get("email");
            String name = (String) userInfo.get("name");

            // Step 3: Check if user exists
            User user = userService.getUserByEmail(email);

            if (user == null) {
                // Step 4: Register new user
                user = new User();
                user.setEmail(email);
                user.setFirstname(name != null ? name.split(" ")[0] : "Google");
                user.setLastname(name != null && name.split(" ").length > 1 ? name.split(" ")[1] : "");
                user.setPassword(UUID.randomUUID().toString()); // random temp password
                user.setStatus(Status.ACTIVE);
                user.setJoinedDate(new Date());
                user.setTransactions(null);

                try {
                    user = userService.addUser(user);
                    log.info("New user created via Google OAuth: {}", user);
                } catch (Exception e) {
                    log.error("Failed to create user from Google OAuth", e);
                    redirectAttributes.addFlashAttribute("error",
                            "Failed to create account. Please try again or use another method.");
                    return "redirect:/login";
                }
            } else {
                log.info("Existing user logged in via Google OAuth: {}", user);
            }

            // Step 5: Set session
            Map<String, String> tokens = JwtUtil.generateTokens(user.getId(), Role.Organization);
            session.setAttribute("accessToken", tokens.get("access_token"));
            session.setAttribute("refreshToken", tokens.get("refresh_token"));
            session.setAttribute("role", Role.User);
            session.setAttribute("id", user.getId());
            session.setAttribute("email", user.getEmail());
            model.addAttribute("connected", session.getAttribute("role") != null);

            return "redirect:/home";

        } catch (Exception e) {
            log.error("Google OAuth2 login failed", e);
            redirectAttributes.addFlashAttribute("error",
                    "Google login failed. Please try again.");
            return "redirect:/login";
        }
    }

}