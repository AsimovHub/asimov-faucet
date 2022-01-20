package ac.asimov.faucet;

import ac.asimov.faucet.blockchain.MultiVACBlockchainGateway;
import ac.asimov.faucet.dao.FaucetClaimDao;
import ac.asimov.faucet.service.FaucetService;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ContextConfiguration(classes = { FaucetApplication.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {
		FaucetService.class, FaucetClaimDao.class, MultiVACBlockchainGateway.class })
@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
// @AutoConfigureCache
@AutoConfigureDataJpa
// @AutoConfigureTestEntityManager
// @DataJpaTest
class MultiVACBlockchainGatewayTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MultiVACBlockchainGateway blockchainGateway;
}
