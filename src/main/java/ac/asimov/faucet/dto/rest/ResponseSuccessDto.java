package ac.asimov.faucet.dto.rest;

import org.apache.commons.lang3.StringUtils;

public class ResponseSuccessDto {
    private String message;

    public ResponseSuccessDto() {
        this.message = "";
    }

    public ResponseSuccessDto(String successMessage) {
        this.message = successMessage;
    }

    public boolean hasSuccess() {
        return !StringUtils.isBlank(this.message);
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}