package vpn.exceptions;

import java.io.IOException;

public class UnexpectedResponseException extends IOException {

    public UnexpectedResponseException(String message) {
        super(message);
    }
}
