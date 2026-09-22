package lk.fintrex.drpapi.dto;

import jakarta.validation.constraints.NotBlank;

public record DrpSearchRequest(
        @NotBlank(message = "nic is required")
        String nic,
        String name,
        String address
) {
}
