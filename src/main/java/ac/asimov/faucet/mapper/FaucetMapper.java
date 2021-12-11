package ac.asimov.faucet.mapper;


import ac.asimov.faucet.dto.rest.FaucetClaimResponseDto;
import ac.asimov.faucet.model.FaucetClaim;
import org.springframework.stereotype.Component;

@Component
public class FaucetMapper {

    public FaucetClaimResponseDto mapFaucetClaimToFaucetClaimResponseDto(FaucetClaim faucetClaim) {
        FaucetClaimResponseDto responseDto = new FaucetClaimResponseDto();

        responseDto.setReceivingAddress(faucetClaim.getReceivingAddress());
        responseDto.setReceivingAmount(faucetClaim.getReceivingAmount());
        responseDto.setReceivingCurrency(faucetClaim.getClaimedCurrency());


        return responseDto;
    }


}
