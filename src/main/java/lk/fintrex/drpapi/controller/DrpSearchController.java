package lk.fintrex.drpapi.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;

import lk.fintrex.drpapi.dto.ApiResponse;
import lk.fintrex.drpapi.dto.DrpSearchRequest;
import lk.fintrex.drpapi.dto.DrpSearchResponse;
import lk.fintrex.drpapi.service.DrpSearchService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drp")
@SecurityRequirement(name = "bearerAuth")
public class DrpSearchController {

    private static final String NO_DATA_MESSAGE =
            "No DRP customer found for the supplied NIC.";

    private final DrpSearchService drpSearchService;

    public DrpSearchController(
            DrpSearchService drpSearchService
    ) {
        this.drpSearchService = drpSearchService;
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<DrpSearchResponse>> search(
            @Valid @RequestBody DrpSearchRequest request
    ) {

        DrpSearchResponse data =
                drpSearchService.search(request);

        if (data == null) {
            return ResponseEntity.ok(
                    ApiResponse.empty(
                            NO_DATA_MESSAGE
                    )
            );
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        "DRP customer data retrieved successfully.",
                        data
                )
        );
    }
}
