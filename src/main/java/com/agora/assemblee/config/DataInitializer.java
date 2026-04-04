package com.agora.assemblee.config;

import com.agora.assemblee.auth.model.Role;
import com.agora.assemblee.auth.model.User;
import com.agora.assemblee.auth.repository.RoleRepository;
import com.agora.assemblee.auth.repository.UserRepository;
import com.agora.assemblee.institution.model.AssemblySession;
import com.agora.assemblee.institution.model.Committee;
import com.agora.assemblee.institution.repository.AssemblySessionRepository;
import com.agora.assemblee.institution.repository.CommitteeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AssemblySessionRepository sessionRepository;
    private final CommitteeRepository committeeRepository;

    @Override
    public void run(String... args) {
        Role adminRole = ensureRole("ROLE_ADMIN", "Administrateur système");
        ensureRole("ROLE_SECRETARIAT_GENERAL", "Secrétariat général");
        ensureRole("ROLE_DEPUTE", "Député");
        ensureRole("ROLE_QUESTURE", "Questure");

        if (sessionRepository.count() == 0) {
            AssemblySession session = new AssemblySession();
            session.setTitle("14e législature - session ordinaire");
            session.setSessionType("ORDINARY");
            session.setYearLabel(LocalDate.now().getYear());
            session.setStartDate(LocalDate.now());
            sessionRepository.save(session);

            Committee committee = new Committee();
            committee.setName("Commission des lois, des affaires administratives et des droits de l'homme");
            committee.setCode("CLAADH");
            committee.setCommitteeType("PERMANENT");
            committee.setActiveSession(session);
            committeeRepository.save(committee);
        }

        if (userRepository.findByEmailIgnoreCase("admin@agora.ga").isEmpty()) {
            User user = new User();
            user.setUsername("admin");
            user.setEmail("admin@agora.ga");
            user.setPassword(passwordEncoder.encode("Admin@12345"));
            user.setFullName("Administrateur AGORA");
            user.setRoles(Set.of(adminRole));
            userRepository.save(user);
        }
    }

    private Role ensureRole(String name, String description) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            role.setDescription(description);
            return roleRepository.save(role);
        });
    }
}
