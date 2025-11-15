package com.dara.su79;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SchoolManagementApplicationTests {

    @Mock
    private Keycloak keycloak;

    @Mock
    private RealmResource realmResource;


    @Test
	void contextLoads() {
	}

}
