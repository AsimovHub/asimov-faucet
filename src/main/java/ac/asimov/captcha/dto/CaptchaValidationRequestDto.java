package ac.asimov.captcha.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CaptchaValidationRequestDto {

    private String secret;
    private String response;

    @JsonProperty("remoteip")
    private String remoteIp;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }
}
