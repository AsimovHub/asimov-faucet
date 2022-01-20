package ac.asimov.faucet.blockchain;

import ac.asimov.faucet.dto.AccountBalanceDto;
import ac.asimov.faucet.dto.WalletAccountDto;
import ac.asimov.faucet.blockchain.contracts.AsimovToken;
import ac.asimov.faucet.dto.rest.ResponseWrapperDto;
import ac.asimov.faucet.dto.rest.TransactionResponseDto;
import ac.asimov.faucet.dto.rest.TransferRequestDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
public class MultiVACBlockchainGateway {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${mtv.rpcUrl}")
    private String rpcUrl;

    @Value("${mtv.chainId}")
    private String chainId;

    @Value("${isaac.contractAddress}")
    private String isaacTokenAddress;

    public ResponseWrapperDto<AccountBalanceDto> getISAACAccountBalance(WalletAccountDto account) {
        return getTokenAccountBalance(isaacTokenAddress, account);
    }

    public ResponseWrapperDto<AccountBalanceDto> getTokenAccountBalance(String tokenAddress, WalletAccountDto account) {
        try {
            if (account == null) {
                return new ResponseWrapperDto<>("Account is null");
            }
            Web3j web3 = Web3j.build(new HttpService(rpcUrl));
            Credentials credentials = Credentials.create(Keys.createEcKeyPair());
            AsimovToken contract = AsimovToken.load(tokenAddress, web3, credentials, new StaticGasProvider(DefaultGasProvider.GAS_PRICE, BigInteger.valueOf(3_000_000)));
            BigInteger balance = contract.balanceOf(account.getReceiverAddress()).send();

            if (balance == null) {
                throw new Exception("Error while checking balance");
            }

            BigDecimal balanceInEther = Convert.fromWei(balance.toString(), Convert.Unit.ETHER);

            return new ResponseWrapperDto<>(new AccountBalanceDto(balanceInEther));
        } catch (Exception e) {
            e.printStackTrace();
            if (StringUtils.isBlank(e.getMessage())) {
                return new ResponseWrapperDto<>("RPC error");
            } else {
                return new ResponseWrapperDto<>(e.getMessage());
            }
        }
    }

    public ResponseWrapperDto<TransactionResponseDto> sendISAACTokenFunds(TransferRequestDto request) {
        return sendTokenFunds(isaacTokenAddress, request);
    }

    public ResponseWrapperDto<TransactionResponseDto> sendTokenFunds(String tokenAddress, TransferRequestDto request) {
        try {
            Web3j web3 = Web3j.build(new HttpService(rpcUrl));
            EthGetTransactionCount ethGetTransactionCount = web3
                    .ethGetTransactionCount(request.getSender().getReceiverAddress(), DefaultBlockParameterName.LATEST).send();


            Credentials credentials = Credentials.create(request.getSender().getPrivateKey());
            BigDecimal weiAmount = Convert.toWei(request.getAmount(), Convert.Unit.ETHER);
            // Encode the function
            final Function function = new Function(
                    AsimovToken.FUNC_TRANSFER,
                    Arrays.<Type>asList(
                            new org.web3j.abi.datatypes.Address(request.getReceiver().getReceiverAddress()),
                            new org.web3j.abi.datatypes.generated.Uint256(weiAmount.toBigInteger())),
                    Collections.<TypeReference<?>>emptyList());
            String encodedFunction = FunctionEncoder
                    .encode(function);

            BigInteger gasPrice = Convert.toWei("5", Convert.Unit.GWEI).toBigInteger();
            BigInteger gasLimit = BigInteger.valueOf(100_000);
            logger.info("Current gas limit is: " + gasLimit);
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, tokenAddress, BigInteger.ZERO, encodedFunction);

            // Sign the Transaction
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Long.parseLong(chainId), credentials);
            String hexValue = Numeric.toHexString(signedMessage);

            // Send the Transaction
            org.web3j.protocol.core.methods.response.EthSendTransaction transactionResponse = web3.ethSendRawTransaction(hexValue).sendAsync().get(1000 * 15, TimeUnit.MILLISECONDS);
            if (transactionResponse.hasError()) {
                logger.error(transactionResponse.getError().getMessage());
                return new ResponseWrapperDto<>(transactionResponse.getError().getMessage());
            }
            if (StringUtils.isBlank(transactionResponse.getTransactionHash())) {
                return new ResponseWrapperDto<>("Transaction error");
            }
            return new ResponseWrapperDto<>(new TransactionResponseDto(transactionResponse.getTransactionHash()));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseWrapperDto<>("RPC error");
        }
    }

    public ResponseWrapperDto<AccountBalanceDto> getMTVAccountBalance(WalletAccountDto account) {
        try {
            logger.info("Fetching MTV account balance for " + account.getReceiverAddress());
            Web3j web3 = Web3j.build(new HttpService(rpcUrl));
            EthGetBalance result = web3.ethGetBalance(account.getReceiverAddress(), DefaultBlockParameter.valueOf("latest")).send();
            BigDecimal balanceInEther = Convert.fromWei(result.getBalance().toString(), Convert.Unit.ETHER);
            return new ResponseWrapperDto<>(new AccountBalanceDto(balanceInEther));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseWrapperDto<>(e.getMessage());
        } catch (Error e) {
            e.printStackTrace();
            return new ResponseWrapperDto<>("RPC error");
        }
    }

    public ResponseWrapperDto<TransactionResponseDto> sendMTVFunds(TransferRequestDto request) {
        try {
            Web3j web3 = Web3j.build(new HttpService(rpcUrl));

            BigDecimal weiAmount = Convert.toWei(request.getAmount(), Convert.Unit.ETHER);
            Credentials credentials = Credentials.create(request.getSender().getPrivateKey());

            BigInteger gasLimit = BigInteger.valueOf(21000);

            BigInteger gasPrice = Convert.toWei("5", Convert.Unit.GWEI).toBigInteger();
            EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
            BigInteger nonce =  ethGetTransactionCount.getTransactionCount();

            RawTransaction rawTransaction  = RawTransaction.createEtherTransaction(
                    nonce,
                    gasPrice,
                    gasLimit,
                    request.getReceiver().getReceiverAddress(),
                    weiAmount.toBigInteger());
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Integer.parseInt(chainId), credentials);

            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).send();

            if (ethSendTransaction.hasError()) {
                String errorMessage = ethSendTransaction.getError() != null ? ethSendTransaction.getError().getMessage() : "Blockchain error";
                if (StringUtils.equals(errorMessage, "replacement transaction underpriced")) {
                    errorMessage = "Please wait some time to process the current transaction";
                } else if (StringUtils.equals(errorMessage, "already known")) {
                    errorMessage = "Cannot check price";
                }
                logger.error(errorMessage);
                return new ResponseWrapperDto<>(errorMessage);
            }

            String transactionHash = ethSendTransaction.getTransactionHash();
            return new ResponseWrapperDto<>(new TransactionResponseDto(transactionHash));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseWrapperDto<>("RPC error");
        }
    }

    public boolean isWalletValid(WalletAccountDto walletAccount) {
        return WalletUtils.isValidAddress(walletAccount.getReceiverAddress());
    }
}
