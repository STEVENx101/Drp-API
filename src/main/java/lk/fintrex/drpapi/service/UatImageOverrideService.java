package lk.fintrex.drpapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class UatImageOverrideService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    UatImageOverrideService.class
            );

    private final boolean enabled;

    private final String frontImageBase64;
    private final String backImageBase64;
    private final String photoImageBase64;

    public UatImageOverrideService(
            @Value("${drp.uat.image-override.enabled:false}")
            boolean enabled
    ) throws IOException {

        this.enabled = enabled;

        this.frontImageBase64 =
                readBase64Resource(
                        "uat/nic-front.base64"
                );

        this.backImageBase64 =
                readBase64Resource(
                        "uat/nic-back.base64"
                );

        this.photoImageBase64 =
                readBase64Resource(
                        "uat/photo-image.base64"
                );
    }

    @PostConstruct
    public void logMode() {

        if (enabled) {

            log.warn(
                    "DRP UAT image override is ENABLED. "
                    + "frontImage, backImage and photoImage "
                    + "Base64 values will be replaced."
            );

        } else {

            log.info(
                    "DRP UAT image override is DISABLED. "
                    + "Original DRP response is unchanged."
            );
        }
    }

    /**
     * UAT only.
     *
     * If these fields are present in the original DRP JSON:
     *
     * frontImage
     * backImage
     * photoImage
     *
     * their values are replaced with the UAT Base64 values.
     *
     * No URL is returned.
     * No image endpoint is used.
     *
     * LIVE/default:
     * original DRP response is returned unchanged.
     */
    public JsonNode apply(JsonNode root) {

        if (!enabled || root == null) {
            return root;
        }

        replaceImagesRecursively(root);

        return root;
    }

    private void replaceImagesRecursively(
            JsonNode node
    ) {

        if (node == null) {
            return;
        }

        if (node.isObject()) {

            ObjectNode objectNode =
                    (ObjectNode) node;

            Iterator<Map.Entry<String, JsonNode>>
                    fields = objectNode.fields();

            while (fields.hasNext()) {

                Map.Entry<String, JsonNode> entry =
                        fields.next();

                String fieldName =
                        entry.getKey();

                JsonNode value =
                        entry.getValue();

                if ("frontImage".equals(fieldName)
                        && isBase64Value(value)) {

                    objectNode.put(
                            fieldName,
                            frontImageBase64
                    );

                    continue;
                }

                if ("backImage".equals(fieldName)
                        && isBase64Value(value)) {

                    objectNode.put(
                            fieldName,
                            backImageBase64
                    );

                    continue;
                }

                if ("photoImage".equals(fieldName)
                        && isBase64Value(value)) {

                    objectNode.put(
                            fieldName,
                            photoImageBase64
                    );

                    continue;
                }

                replaceImagesRecursively(value);
            }

        } else if (node.isArray()) {

            ArrayNode arrayNode =
                    (ArrayNode) node;

            for (JsonNode child : arrayNode) {

                replaceImagesRecursively(child);
            }
        }
    }

    /**
     * Only replace fields that actually contain Base64-like text.
     */
    private boolean isBase64Value(
            JsonNode value
    ) {

        if (value == null
                || !value.isTextual()) {

            return false;
        }

        String text =
                value.asText().trim();

        if (text.isBlank()) {
            return false;
        }

        if (text.startsWith("data:image/")) {
            return true;
        }

        /*
         * Common Base64 image prefixes:
         *
         * JPEG = /9j/
         * PNG  = iVBOR
         * GIF  = R0lGOD
         */
        return text.startsWith("/9j/")
                || text.startsWith("iVBOR")
                || text.startsWith("R0lGOD");
    }

    private String readBase64Resource(
            String path
    ) throws IOException {

        ClassPathResource resource =
                new ClassPathResource(path);

        String raw =
                new String(
                        resource
                                .getInputStream()
                                .readAllBytes(),
                        StandardCharsets.UTF_8
                );

        /*
         * Remove line breaks / spaces so the API returns
         * one clean Base64 string.
         */
        return raw.replaceAll("\\s+", "");
    }
}
