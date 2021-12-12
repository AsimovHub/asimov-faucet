package ac.asimov.faucet.controller;

import ac.asimov.faucet.dto.rest.FaucetClaimRequestDto;
import ac.asimov.faucet.dto.rest.FaucetClaimResponseDto;
import ac.asimov.faucet.dto.rest.ResponseWrapperDto;
import ac.asimov.faucet.service.FaucetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@CrossOrigin(allowedHeaders = "*", originPatterns = "*", maxAge = 3600, methods = {GET, POST, OPTIONS, PUT, DELETE})
public class FaucetController {

    @Autowired
    private FaucetService service;


    @RequestMapping(method = RequestMethod.POST, value = "/faucet/claim", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseWrapperDto<FaucetClaimResponseDto> claimFaucet(@RequestBody FaucetClaimRequestDto claimRequest, HttpServletRequest request) {
        claimRequest.setIpAddress(request.getRemoteAddr());
        return service.claimFaucet(claimRequest);
    }


    /*

    @RequestMapping(method = RequestMethod.POST, value = "/tokendeployer/init", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseWrapperDto<TokenDeploymentResponseDto> addTokenDeploymentRequest(@RequestBody TokenDeploymentRequestDto request) {
        return service.addTokenDeploymentRequest(request);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/tokendeployer/trigger", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseWrapperDto<TokenDeploymentResponseDto> triggerTokenDeployment(@RequestBody TriggerTokenDeploymentRequest request) {
        return service.triggerTokenDeployment(request);
    }

     */
}
