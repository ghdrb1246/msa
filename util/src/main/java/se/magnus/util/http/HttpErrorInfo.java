package se.magnus.util.http;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class HttpErrorInfo {
    private ZonedDateTime timestamp;
    private String path;
    private HttpStatus httpStatus;
    private String message;

    public HttpErrorInfo() {
        timestamp = null;
        this.httpStatus = null;
        this.path = null;
        this.message = null;
    }

    public HttpErrorInfo(HttpStatus httpStatus, String path, String message) {
        timestamp = ZonedDateTime.now();
        this.httpStatus = httpStatus;
        this.path = path;
        this.message = message;
    }
}