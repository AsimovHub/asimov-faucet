package ac.asimov.captcha.helper;

import ac.asimov.captcha.dto.CaptchaValidationRequestDto;
import ac.asimov.captcha.dto.CaptchaValidationResponseDto;
import ac.asimov.faucet.dto.rest.ResponseWrapperDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.regex.Pattern;


@Component
public class CaptchaValidationHelper {

    private final static String captchaBaseUrl = "https://www.google.com/recaptcha/api/siteverify";

    @Value("${faucet.captcha}")
    private String captchaSecret;

    private static Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");


    public ResponseWrapperDto<CaptchaValidationResponseDto> validateCaptcha(String token, String ipAddress) {
        CaptchaValidationRequestDto captchaRequest = new CaptchaValidationRequestDto();
        captchaRequest.setResponse(token);
        captchaRequest.setSecret(captchaSecret);
        captchaRequest.setRemoteIp(ipAddress);
        try {
            RestTemplate restTemplate = prepareRestTemplate();

            if(!responseSanityCheck(token)) {
                return new ResponseWrapperDto<>("Captcha code contains invalid characters");
            }

            URI verifyUri = URI.create(String.format(
                    "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s",
                    captchaSecret, token, ipAddress));

            CaptchaValidationResponseDto captchaValidationResponseDto = restTemplate.getForObject(verifyUri, CaptchaValidationResponseDto.class);

            if (!captchaValidationResponseDto.isSuccess()) {
                return new ResponseWrapperDto<>("reCaptcha was not successfully validated");
            } else {
                return new ResponseWrapperDto<>(captchaValidationResponseDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseWrapperDto<>("Error during captcha validation");
        }
    }


    private boolean responseSanityCheck(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

    public RestTemplate prepareRestTemplate() {
        return new RestTemplateBuilder().build();
    }

    public HttpEntity<String> prepareHttpEntity(CaptchaValidationRequestDto captchaRequest) throws Exception {
        ObjectMapper jsonMapper = new ObjectMapper();
        captchaRequest.setRemoteIp(null);
        jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String jsonString = jsonMapper.writeValueAsString(captchaRequest);
        return new HttpEntity<>(jsonString, createJsonHeader());
    }

    public HttpHeaders createJsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json; utf-8");
        headers.add("Host", "asimov.ac");
        headers.add("Cache-Control", "no-cache");
        headers.add("Content-Type", "application/json");
        headers.add("Connection", "keep-alive");
        headers.add("Accept-Encoding", "identity");
        return headers;
    }


}
