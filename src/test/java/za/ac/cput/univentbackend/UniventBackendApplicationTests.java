package za.ac.cput.univentbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:univent-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.username=root",
        "spring.datasource.password=password",
        "jwt.secret=univent-context-test-secret-key-long-enough",
        "jwt.expiration=3600000"
})
class UniventBackendApplicationTests {

    @DynamicPropertySource
    static void useIsolatedDatabaseCredentials(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
    }

    @Test
    void contextLoads() {
    }

}
