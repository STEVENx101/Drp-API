package lk.fintrex.drpapi.dto;

public record ApiResponse<T>(
        int status,
        String message,
        T data
) {

    public static <T> ApiResponse<T> success(
            String message,
            T data
    ) {
        return new ApiResponse<>(
                200,
                message,
                data
        );
    }

    public static <T> ApiResponse<T> empty(
            String message
    ) {
        return new ApiResponse<>(
                200,
                message,
                null
        );
    }
}
