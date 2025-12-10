package ru.practicum.shareit.client;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path) {
        return get(path, null, null);
    }

    protected ResponseEntity<Object> get(String path, long userId) {
        return get(path, userId, null);
    }

    public ResponseEntity<Object> get(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    protected <T> ResponseEntity<Object> post(String path, T body) {
        return post(path, null, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, long userId, T body) {
        return post(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body);
    }

    protected <T> ResponseEntity<Object> put(String path, long userId, T body) {
        return put(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> put(String path, long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PUT, path, userId, parameters, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, T body) {
        return patch(path, null, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, long userId) {
        return patch(path, userId, null, null);
    }

    protected <T> ResponseEntity<Object> patch(String path, long userId, T body) {
        return patch(path, userId, null, body);
    }

    public <T> ResponseEntity<Object> patch(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body);
    }

    protected ResponseEntity<Object> delete(String path) {
        return delete(path, null, null);
    }

    protected ResponseEntity<Object> delete(String path, long userId) {
        return delete(path, userId, null);
    }

    protected ResponseEntity<Object> delete(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Long userId,
                                                          @Nullable Map<String, Object> parameters, @Nullable T body) {

        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<Object> response;

        try {
            if (parameters != null) {
                response = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                response = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        }

        return response;
    }


    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    protected <T> ResponseEntity<T> getTyped(String path, Long userId, Class<T> responseType) {
        return getTyped(path, userId, null, responseType);
    }

    protected <T> ResponseEntity<T> getTyped(String path, Long userId, @Nullable Map<String, Object> parameters, Class<T> responseType) {
        HttpEntity<Void> request = new HttpEntity<>(defaultHeaders(userId));
        try {
            if (parameters != null) {
                return rest.exchange(path, HttpMethod.GET, request, responseType, parameters);
            }
            return rest.exchange(path, HttpMethod.GET, request, responseType);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }

    protected <T, R> ResponseEntity<R> post(String path, Long userId, T body, Class<R> responseType) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<R> response;
        try {
                response = rest.exchange(path, HttpMethod.POST, requestEntity, responseType);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }

        return response;
    }

    protected <T, R> ResponseEntity<R> postTyped(String path, Long userId, T body, Class<R> responseType) {
        return postTyped(path, userId, null, body, responseType);
    }

    protected <T, R> ResponseEntity<R> postTyped(String path, Long userId, @Nullable Map<String, Object> parameters, T body, Class<R> responseType) {
        HttpEntity<T> request = new HttpEntity<>(body, defaultHeaders(userId));
        try {
            if (parameters != null) {
                return rest.exchange(path, HttpMethod.POST, request, responseType, parameters);
            }
            return rest.exchange(path, HttpMethod.POST, request, responseType);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }

    protected <T, R> ResponseEntity<R> patchTyped(String path, Long userId, Class<R> responseType) {
        return patchTyped(path, userId, null, null, responseType);
    }

    protected <T, R> ResponseEntity<R> patchTyped(String path, Long userId, @Nullable Map<String, Object> parameters, T body, Class<R> responseType) {
        HttpEntity<T> request = new HttpEntity<>(body, defaultHeaders(userId));
        try {
            if (parameters != null) {
                return rest.exchange(path, HttpMethod.PATCH, request, responseType, parameters);
            }
            return rest.exchange(path, HttpMethod.PATCH, request, responseType);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }

    protected <T, R> ResponseEntity<R> patchTyped(String path,
                                                  Long userId,
                                                  T body,
                                                  Class<R> responseType) {
        return patchTyped(path, userId, null, body, responseType);
    }

    protected <T> ResponseEntity<List<T>> getListTyped(String path, Long userId, Class<T> elementType) {
        return getListTyped(path, userId, null, elementType);
    }

    protected <T> ResponseEntity<List<T>> getListTyped(String path, Long userId, @Nullable Map<String, Object> parameters, Class<T> elementType) {
        ParameterizedTypeReference<List<T>> typeRef = new ParameterizedTypeReference<>() {};
        HttpEntity<Void> request = new HttpEntity<>(defaultHeaders(userId));
        try {
            if (parameters != null) {
                return rest.exchange(path, HttpMethod.GET, request, typeRef, parameters);
            }
            return rest.exchange(path, HttpMethod.GET, request, typeRef);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }
}