package com.psicoagenda.psicoagendaapi.services;

import com.psicoagenda.psicoagendaapi.models.User;
import com.psicoagenda.psicoagendaapi.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Importação necessária
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // Injeção via construtor
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User save(User user) {

        if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty() && !user.getPasswordHash().startsWith("$2a$")) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }

        return userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}