package ac.asimov.faucet.controller;

import ac.asimov.faucet.dto.rest.FaucetInformationDto;
import ac.asimov.faucet.dto.rest.ResponseWrapperDto;
import ac.asimov.faucet.dto.rest.WalletInformationDto;
import ac.asimov.faucet.service.FaucetStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@CrossOrigin(allowedHeaders = "*", originPatterns = "*", maxAge = 3600, methods = {GET, POST, OPTIONS, PUT, DELETE})
public class FaucetStatisticsController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FaucetStatisticsService statisticsService;

    @RequestMapping(method = RequestMethod.GET, value = "/faucet/wallet/{walletAddress}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseWrapperDto<WalletInformationDto> getWalletInformation(@PathVariable(name = "walletAddress") String walletAddress) {
        return statisticsService.getWalletInformation(walletAddress);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/faucet/stats", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseWrapperDto<FaucetInformationDto> getFaucetInformation() {
        return statisticsService.getFaucetInformation();
    }
}
