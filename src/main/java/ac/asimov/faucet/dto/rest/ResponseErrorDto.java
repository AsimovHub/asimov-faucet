package ac.asimov.faucet.dto.rest;

import org.apache.commons.lang3.StringUtils;

public class ResponseErrorDto {
    private String message;

    public ResponseErrorDto() {
        this.message = "";
    }

    public ResponseErrorDto(String errorMessage) {
        this.message = errorMessage;
    }

    public boolean hasError() {
        return !StringUtils.isBlank(this.message);
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
