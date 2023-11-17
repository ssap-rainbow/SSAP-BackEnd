package ssap.ssap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponseDto<T> {
    private String message;
    private T data;

    public static <T> ErrorResponseDto<T> error(String message, T data) {
        return new ErrorResponseDto<>(message, data);
    }
}
