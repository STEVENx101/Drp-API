package lk.fintrex.drpapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;

import lk.fintrex.drpapi.dto.DrpSearchRequest;
import lk.fintrex.drpapi.dto.DrpSearchResponse;
import lk.fintrex.drpapi.repository.DrpRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DrpSearchService {

    private final DrpRepository drpRepository;
    private final ObjectMapper objectMapper;
    private final UatImageOverrideService uatImageOverrideService;

    public DrpSearchService(
            DrpRepository drpRepository,
            ObjectMapper objectMapper,
            UatImageOverrideService uatImageOverrideService
    ) {
        this.drpRepository = drpRepository;
        this.objectMapper = objectMapper;
        this.uatImageOverrideService = uatImageOverrideService;
    }

    @Transactional(readOnly = true)
    public DrpSearchResponse search(
            DrpSearchRequest request
    ) {

        String nic = request.nic().trim();

        Long drpId =
                drpRepository
                        .findCustomerIdByNic(nic)
                        .orElse(null);

        if (drpId == null) {
            return null;
        }

        Long requestId =
                drpRepository
                        .findLatestRequestIdByCustomer(drpId)
                        .orElse(null);

        if (requestId == null) {
            return null;
        }

        String rawResponse =
                drpRepository
                        .findResponseByRequestId(requestId)
                        .orElse(null);

        if (rawResponse == null
                || rawResponse.isBlank()) {
            return null;
        }

        JsonNode response =
                toJsonNode(rawResponse);

        response =
                uatImageOverrideService.apply(response);

        return new DrpSearchResponse(
                drpId,
                requestId,
                response
        );
    }

    private JsonNode toJsonNode(
            String rawResponse
    ) {

        try {
            return objectMapper.readTree(rawResponse);

        } catch (Exception ex) {
            return TextNode.valueOf(rawResponse);
        }
    }
}
