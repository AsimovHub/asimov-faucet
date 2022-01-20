package ac.asimov.faucet;

import ac.asimov.faucet.blockchain.MultiVACBlockchainGateway;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@ContextConfiguration(classes = { FaucetApplication.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {
		MultiVACBlockchainGateway.class })
@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
@AutoConfigureDataJpa
class FaucetApplicationTests {
	
	@Test
	void contextLoads() {
	}

}
