package com.dara.su79.services;

import com.dara.su79.dto.RegisterRequest;
import com.dara.su79.exceptions.MyResourceNotFoundException;
import com.dara.su79.models.Student;
import com.dara.su79.models.Teacher;
import com.dara.su79.models.User;
import com.dara.su79.models.UserProfile;
import com.dara.su79.repositories.StudentRepository;
import com.dara.su79.repositories.UserRepository;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final RealmResource realmResource;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       StudentRepository studentRepository,
                       RealmResource realmResource,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.realmResource = realmResource;
        this.passwordEncoder = passwordEncoder;
    }

    // ----------------- Public Methods -----------------

    @Transactional
    public User createUser(User user) {
        return createUser(user, "123@"); // default password if not provided
    }

    @Transactional
    public User createUser(User user, String rawPassword) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");

        // Encode password for local DB
        user.setPassword(passwordEncoder.encode(rawPassword));

        // Link UserProfile if exists
        if (user.getUserProfile() != null) {
            user.getUserProfile().setUser(user);
        }

        // Save locally first
        userRepository.save(user);
        logger.info("User saved in local DB: {}", user.getUsername());

        // Create in Keycloak
        createUserInKeycloak(user, rawPassword);

        return user;
    }

    @Transactional
    public User createTeacherUser(User user) {
        if (!"TEACHER".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("This method is only for TEACHER role");
        }

        Teacher teacher = new Teacher();
        teacher.setFirstName(user.getFirstName());
        teacher.setLastName(user.getLastName());
        teacher.setEmail(user.getEmail());
        teacher.setGender("Male"); // default
        teacher.setUser(user);

        user.setTeacher(teacher);

        return createUser(user, user.getPassword());
    }



    @Transactional
    public User createStudentUser(User user) {
        if (!"STUDENT".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("This method is only for STUDENT role");
        }

        Student student = new Student();
        student.setFirstName(user.getFirstName());
        student.setLastName(user.getLastName());
        student.setEmail(user.getEmail());
        student.setGender("Male"); // default
        student.setUser(user);

        user.setStudent(student);

        return createUser(user, user.getPassword());
    }

    public List<User> searchUsersByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
    }

    public Page<User> getUsersPaged(int page, int size, String username) {
        Pageable pageable = PageRequest.of(page, size);

        if (username != null && !username.isBlank()) {
            return userRepository.findByUsernameContainingIgnoreCase(username, pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }



    @Transactional
    public User registerStudent(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setRole("STUDENT");
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        String rawPassword = request.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));

        Student student = new Student();
        student.setFirstName(user.getFirstName());
        student.setLastName(user.getLastName());
        student.setEmail(user.getEmail());
        student.setGender("Male"); // default
        student.setUser(user);

        user.setStudent(student);

        // Link UserProfile if exists
        if (user.getUserProfile() != null) {
            user.getUserProfile().setUser(user);
        }

        userRepository.save(user);
        createUserInKeycloak(user, rawPassword);

        return user;
    }

    public List<User> getUserAll() {
        return userRepository.findAll();
    }

    public User findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(int id) {
        User user = findById(id);

        // Delete from Keycloak
        if (user.getKeycloakId() != null) {
            try {
                realmResource.users().get(user.getKeycloakId()).remove();
                logger.info("Deleted user {} from Keycloak", user.getUsername());
            } catch (Exception e) {
                logger.error("Error deleting user in Keycloak: {}", e.getMessage(), e);
            }
        }

        // Delete locally
        userRepository.delete(user);
        logger.info("Deleted user {} from local DB", user.getUsername());
    }

    // ----------------- Private Helper -----------------

    private void createUserInKeycloak(User user, String rawPassword) {
        try {
            UsersResource usersResource = realmResource.users();

            // -------- 1. Check if user already exists in Keycloak --------
            List<UserRepresentation> existingUsers = usersResource.search(user.getUsername(), true);
            if (!existingUsers.isEmpty()) {
                // User exists in Keycloak: take the first match
                String existingKcId = existingUsers.get(0).getId();
                user.setKeycloakId(existingKcId);
                userRepository.save(user);
                logger.warn("User '{}' already exists in Keycloak. Using existing ID.", user.getUsername());
                return;
            }

            // -------- 2. Create Keycloak user --------
            UserRepresentation kcUser = new UserRepresentation();
            kcUser.setUsername(user.getUsername());
            kcUser.setEmail(user.getEmail());
            kcUser.setFirstName(user.getFirstName());
            kcUser.setLastName(user.getLastName());
            kcUser.setEnabled(true);

            // Set password (raw password)
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(rawPassword);
            kcUser.setCredentials(Collections.singletonList(passwordCred));

            // Create user
            Response response = usersResource.create(kcUser);
            if (response.getStatus() != 201) {
                response.close();
                throw new RuntimeException("Failed to create user in Keycloak. Status: " + response.getStatus());
            }

            // -------- 3. Extract Keycloak ID and save locally --------
            String kcUserId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            user.setKeycloakId(kcUserId);
            userRepository.save(user);
            response.close();

            // -------- 4. Assign role if exists --------
            if (user.getRole() != null && !user.getRole().isEmpty()) {
                try {
                    RoleRepresentation role = realmResource.roles().get(user.getRole()).toRepresentation();
                    usersResource.get(kcUserId).roles().realmLevel().add(Collections.singletonList(role));
                    logger.info("Assigned role '{}' to user {}", user.getRole(), user.getUsername());
                } catch (Exception e) {
                    logger.warn("Role '{}' not found in Keycloak. Skipping.", user.getRole());
                }
            }

            logger.info("User '{}' created successfully in Keycloak.", user.getUsername());

        } catch (Exception e) {
            throw new RuntimeException("Failed to create user in Keycloak: " + e.getMessage(), e);
        }
    }

}
