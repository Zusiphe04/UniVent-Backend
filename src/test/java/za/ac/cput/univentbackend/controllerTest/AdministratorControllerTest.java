package za.ac.cput.univentbackend.controllerTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import za.ac.cput.controller.AdministratorController;
import za.ac.cput.domain.Administrator;
import za.ac.cput.service.IAdministratorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdministratorControllerTest {

	@Mock
	private IAdministratorService service;

	@InjectMocks
	private AdministratorController controller;

	@Test
	void testCreateAdministrator() {
		Administrator admin = new Administrator.Builder()
				.setAdminLevel("SUPER")
				.build();

		when(service.create(admin)).thenReturn(admin);

		ResponseEntity<Administrator> response = controller.createAdministrator(admin);

		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals("SUPER", response.getBody().getAdminLevel());
		verify(service).create(admin);
	}

	@Test
	void testGetAdministratorById() {
		Administrator admin = new Administrator.Builder()
				.setAdminLevel("LEVEL")
				.build();

		when(service.read("1")).thenReturn(admin);

		ResponseEntity<Administrator> response = controller.getAdministratorById("1");

		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals("LEVEL", response.getBody().getAdminLevel());
		verify(service).read("1");
	}

	@Test
	void testUpdateAdministrator() {
		Administrator admin = new Administrator.Builder()
				.setAdminLevel("UPDATE")
				.build();

		when(service.update(admin)).thenReturn(admin);

		ResponseEntity<Administrator> response = controller.updateAdministrator(admin);

		assertNotNull(response);
		assertNotNull(response.getBody());
		assertEquals("UPDATE", response.getBody().getAdminLevel());
		verify(service).update(admin);
	}

	@Test
	void testDeleteAdministrator() {
		ResponseEntity<Void> response = controller.delete("1");

		assertNotNull(response);
		assertEquals(204, response.getStatusCode().value());
		verify(service).delete("1");
	}
}
