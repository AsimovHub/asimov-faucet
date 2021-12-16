package ac.asimov.captcha.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class CaptchaValidationResponseDto {

    private boolean success;
    private float score;
    private String action;

    @JsonProperty("challenge_ts")
    private LocalDateTime challengeTs;

    private String hostname;

    @JsonProperty("error_codes")
    private List<String> errorCodes;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getChallengeTs() {
        return challengeTs;
    }

    public void setChallengeTs(LocalDateTime challengeTs) {
        this.challengeTs = challengeTs;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public List<String> getErrorCodes() {
        return errorCodes;
    }

    public void setErrorCodes(List<String> errorCodes) {
        this.errorCodes = errorCodes;
    }
}
