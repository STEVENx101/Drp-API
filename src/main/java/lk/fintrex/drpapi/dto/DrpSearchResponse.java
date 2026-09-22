package lk.fintrex.drpapi.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record DrpSearchResponse(
        Long drpId,
        Long requestId,
        JsonNode response
) {
}
