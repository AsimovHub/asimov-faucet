package ac.asimov.faucet.dto.rest;

import org.apache.commons.lang3.StringUtils;

public class ResponseWarningDto {
    private String message;

    public ResponseWarningDto() {
        this.message = "";
    }

    public ResponseWarningDto(String warningMessage) {
        this.message = warningMessage;
    }

    public boolean hasWarning() {
        return !StringUtils.isBlank(this.message);
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}