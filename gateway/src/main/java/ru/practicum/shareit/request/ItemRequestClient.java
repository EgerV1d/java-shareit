package ru.practicum.shareit.request;

import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Setter
    private String appName;

    public ItemRequestClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> createRequest(long userId, CreateItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getUserRequests(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequests(long userId) {
        return get("/all", userId);
    }

    public ResponseEntity<Object> getRequestById(long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}