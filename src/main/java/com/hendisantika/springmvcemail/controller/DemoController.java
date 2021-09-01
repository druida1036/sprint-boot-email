package com.hendisantika.springmvcemail.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@RestController
public class DemoController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("auth")
    public void authenticate() {
        HttpHeaders authRequestHeaders = getAcceptJsonHeader();
        authRequestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add(OAuth2Constants.USER_NAME, "jmartinez@trustcar.com");
        request.add(OAuth2Constants.PASSWORD, "Pre1234563vUif1qwth83oFyyQa2jBeHwS");
        request.add(OAuth2Constants.CLIENT_ID, "3MVG9cHH2bfKACZaP75Fd7lrbHq6oNt_Wt1bhnJArofsKFc2e7yrLF9xgv8H_aPWC9jQtQkxSgW6Da3pbTZf6");
        request.add(OAuth2Constants.CLIENT_SECRET, "17603D6E773C7FFAD035B4F813B54C25D71F2FA88BCC529A4C998103C9FF76CB");
        request.add(OAuth2Constants.GRANT_TYPE, "password");
        ResponseEntity<Object> response;


        try {
            response = restTemplate.postForEntity("https://login.salesforce.com/services/oauth2/token", new HttpEntity(request, authRequestHeaders),
                Object.class);
            System.out.println(response.getBody());
        } catch (RestClientResponseException e) {
            throw new RuntimeException("Unable to verify connection details.");
        }
    }

    public HttpHeaders getAcceptJsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
