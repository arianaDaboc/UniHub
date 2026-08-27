package ro.utcluj.unihub.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Locale;

@Component
public class InstitutionalEmailPolicy {
    private final List<String> personalDomains;

    public InstitutionalEmailPolicy(@Value("${app.registration.personal-email-domains}") List<String> personalDomains) {
        this.personalDomains = personalDomains.stream().map(domain -> domain.trim().toLowerCase(Locale.ROOT)).toList();
    }

    public boolean isAllowed(String email) {
        int separator = email.lastIndexOf('@');
        if (separator < 1 || separator == email.length() - 1) return false;
        String domain = email.substring(separator + 1).toLowerCase(Locale.ROOT);
        return !personalDomains.contains(domain);
    }
}
