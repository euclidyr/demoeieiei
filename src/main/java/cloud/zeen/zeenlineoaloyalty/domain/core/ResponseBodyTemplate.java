package cloud.zeen.zeenlineoaloyalty.domain.core;

import org.springframework.http.HttpStatus;

public class ResponseBodyTemplate<T> {
    private boolean status;
    private int code;
    private String message;
    private T objectValue;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(HttpStatus code) {
        this.code = code.value();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getObjectValue() {
        return objectValue;
    }

    public void setObjectValue(T objectValue) {
        this.objectValue = objectValue;
    }

    public void setOperationError(HttpStatus code, String message, T object) {
        this.status = false;
        this.code = code.value();
        this.message = message;
        this.objectValue = object;
    }

    public void setOperationSuccess(HttpStatus code, String message, T object) {
        this.status = true;
        this.code = code.value();
        this.message = message;
        this.objectValue = object;
    }


}
