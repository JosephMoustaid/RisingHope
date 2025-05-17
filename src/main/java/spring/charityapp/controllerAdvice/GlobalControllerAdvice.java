package spring.charityapp.controllerAdvice;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.Locale;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private LocaleResolver localeResolver;

    @ModelAttribute("currentLang")
    public String getCurrentLang(HttpServletRequest request,
                                 Model model,
                                 HttpSession session) {
        Locale locale = localeResolver.resolveLocale(request);
        model.addAttribute("connected", session.getAttribute("role") != null);
        return locale.getLanguage(); // returns "en", "fr", "ar"
    }
}
