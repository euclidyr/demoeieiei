package cloud.zeen.zeenlineoaloyalty.service;

import cloud.zeen.zeenlineoaloyalty.domain.AddLineUserToZeenDBRequest;
import cloud.zeen.zeenlineoaloyalty.domain.AddLineUserToZeenDBResponse;
import cloud.zeen.zeenlineoaloyalty.domain.LIDToZIDMapping;
import cloud.zeen.zeenlineoaloyalty.domain.core.ResponseBodyTemplate;
import cloud.zeen.zeenlineoaloyalty.exception.ApiException;
import cloud.zeen.zeenlineoaloyalty.helper.Base64Helper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class LineOAService {
    private static final Logger log = LoggerFactory.getLogger(LineOAService.class);

    private static final String SUCCESS = "200";

    @Value("${channel.id}")
    private String CHANNEL_ID;

    @Value("${channel.secret}")
    private String CHANNEL_SECRET;

    @Value("${redirect_uri}")
    private String REDIRECT_URI;

    @Value("${line.issue.token.url}")
    private String LINE_ISSUE_TOKEN_URL;

    @Value("${line.verify.token.url}")
    private String LINE_VERIFY_TOKEN_URL;

    private static final List<LIDToZIDMapping> lidToZidMappings = new ArrayList<>();

    private final RestTemplate restTemplate;

    public LineOAService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String callIssueToken(String code) {
        try {
            // Prepare the headers
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/x-www-form-urlencoded");

            // Prepare the body
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("code", code);
            body.add("client_id", CHANNEL_ID);
            body.add("client_secret", CHANNEL_SECRET);
            body.add("redirect_uri", REDIRECT_URI);

            // Combine headers and body into an HttpEntity
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

            // Create RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

            // Send Post Request
            ResponseEntity<String> response = restTemplate.exchange(
                    LINE_ISSUE_TOKEN_URL,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // Parse the response body into a JSONObject
            JSONObject responseBody = new JSONObject(response.getBody());

            // Return the response body
            return responseBody != null ? responseBody.optString("id_token", null) : null;
        } catch (Exception e) {
            log.error("Error calling LINE API verify token: {}", e.getMessage());
            throw e;
        }
    }

    public String callVerifyToken(String idToken) {
        try {
            // Prepare the headers
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/x-www-form-urlencoded");

            // Prepare the body
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("id_token", idToken);
            body.add("client_id", CHANNEL_ID);

            // Combine headers and body into an HttpEntity
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

            // Create RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

            // Send the POST request
            ResponseEntity<String> response = restTemplate.exchange(
                    LINE_VERIFY_TOKEN_URL,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // Parse the response body into a JSONObject
            JSONObject responseBody = new JSONObject(response.getBody());

            // Return the response body
            return responseBody != null ? responseBody.optString("sub", null) : null;
        } catch (Exception e) {
            log.error("Error calling LINE API issue access token: {}", e.getMessage());
            throw e;
        }
    }

    public ResponseBodyTemplate<AddLineUserToZeenDBResponse> addLineUserToZeenDB(String token, AddLineUserToZeenDBRequest addLineUserToZeenDBRequest) {
        ResponseBodyTemplate<AddLineUserToZeenDBResponse> response = new ResponseBodyTemplate<>();

        try {
            if (token == null || token.trim().isEmpty()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "token cannot be null or empty");
            }

            if (addLineUserToZeenDBRequest == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "request body cannot be null");
            }

            String code = addLineUserToZeenDBRequest.getCode();
            if (code == null || code.trim().isEmpty()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "code cannot be null or empty");
            }

            // call issue token api
            String idToken = callIssueToken(code);
            // call verify token api
            String lineUserId = callVerifyToken(idToken);

            // extract jwt token's payload
            String[] parts = token.split("\\.");
            JSONObject payload = new JSONObject(Base64Helper.decode(parts[1]));

            String zeenUserId = payload.optString("user", null);

            AddLineUserToZeenDBResponse addLineUserToZeenDBResponse = new AddLineUserToZeenDBResponse();

            boolean isLineUserIdExisted = lidToZidMappings.stream()
                    .anyMatch(mapping -> mapping.getLineUserId().equals(lineUserId));
            boolean isZeenUserIdExisted = lidToZidMappings.stream()
                    .anyMatch(mapping -> mapping.getZeenUserId().equals(zeenUserId));
            String message = "";
            if (!isLineUserIdExisted && !isZeenUserIdExisted) {
                lidToZidMappings.add(new LIDToZIDMapping(lineUserId, zeenUserId));
                message = "UID and ZID are added successfully";
            } else if (isLineUserIdExisted) {
                message = "Your LINE User Id already exists in the database";
            } else if (isZeenUserIdExisted) {
                lidToZidMappings.removeIf(mapping -> mapping.getZeenUserId().equals(zeenUserId));
                lidToZidMappings.add(new LIDToZIDMapping(lineUserId, zeenUserId));
                message = "Terminate old mapping and Add the new LINE User Id with the given Zeen User Id in the database";
            }

            addLineUserToZeenDBResponse.setLineUserId(lineUserId);
            addLineUserToZeenDBResponse.setZeenUserId(zeenUserId);
            addLineUserToZeenDBResponse.setCurrentLIDToZIDMappings(lidToZidMappings);
            response.setOperationSuccess(HttpStatus.OK, message, addLineUserToZeenDBResponse);

        } catch (Exception e) {
            log.error("Error in addLineUserToZeenDB: {}", e.getMessage());
            throw e;
        }

        return response;
    }
}
