package elearningspringboot.repository.httpclient;

import elearningspringboot.dto.request.ExchangeTokenRequest;
import elearningspringboot.dto.response.ExchangeTokenResponse;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "google-identity-client", url = "https://oauth2.googleapis.com")
public interface GoogleIdentityClient {
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ExchangeTokenResponse getToken(@QueryMap ExchangeTokenRequest request);
}
