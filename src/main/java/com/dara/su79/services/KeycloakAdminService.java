package com.dara.su79.services;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.Response;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.dara.su79.dto.RegisterRequest;

@Service
public class KeycloakAdminService {

    private final Environment env;

    public KeycloakAdminService(Environment env) {
        this.env = env;
    }

    private Keycloak getAdminInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(env.getProperty("keycloak.server-url"))
                .realm("master")  // admin realm
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(env.getProperty("keycloak.admin.client-id"))
                .username(env.getProperty("keycloak.admin-username"))
                .password(env.getProperty("keycloak.admin-password"))
                .build();
    }

    public String registerUser(RegisterRequest request) {
        Keycloak keycloak = getAdminInstance();

        // Create password credential
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());

        // Create user representation
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setEnabled(true);
        user.setCredentials(Collections.singletonList(credential));

        // Register user in Keycloak
        Response response = keycloak.realm(env.getProperty("keycloak.realm")).users().create(user);

        if (response.getStatus() == 201) {
            String userId = CreatedResponseUtil.getCreatedId(response);

            // Assign default role "STUDENT"
            assignDefaultRole(keycloak, userId, "STUDENT");

            return "User registered successfully with ID: " + userId;
        } else if (response.getStatus() == 409) {
            return "User already exists";
        } else {
            throw new RuntimeException("Failed to register user. Status: " + response.getStatus());
        }
    }
    public void sendResetPasswordEmailByEmail(String email) {
        Keycloak keycloak = getAdminInstance();

        // Search user by email
        List<UserRepresentation> users = keycloak.realm(env.getProperty("keycloak.realm"))
                .users()
                .search(email, true); // 'true' allows search by email

        if (users.isEmpty()) {
            // Optionally, ignore silently to prevent user enumeration
            return;
        }

        String userId = users.get(0).getId();

        // Trigger password reset email
        keycloak.realm(env.getProperty("keycloak.realm"))
                .users()
                .get(userId)
                .executeActionsEmail(List.of("UPDATE_PASSWORD"));
    }



//    public void sendResetPasswordEmail(String username) {
//        Keycloak keycloak = getAdminInstance();
//
//        List<UserRepresentation> users = keycloak.realm(env.getProperty("keycloak.realm"))
//                .users()
//                .search(username);
//
//        if (users.isEmpty()) {
//            throw new RuntimeException("User not found");
//        }
//
//        String userId = users.get(0).getId();
//
//        // Trigger "UPDATE_PASSWORD" action email
//        keycloak.realm(env.getProperty("keycloak.realm"))
//                .users()
//                .get(userId)
//                .executeActionsEmail(List.of("UPDATE_PASSWORD"));
//    }

    private void assignDefaultRole(Keycloak keycloak, String userId, String roleName) {
        RoleRepresentation role = keycloak.realm(env.getProperty("keycloak.realm"))
                .roles()
                .get(roleName)
                .toRepresentation();

        keycloak.realm(env.getProperty("keycloak.realm"))
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(List.of(role));
    }
}
