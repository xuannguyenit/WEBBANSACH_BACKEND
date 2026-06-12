package com.xuannguyen.identity.repository.httpclient;

import com.xuannguyen.identity.dto.response.ProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.xuannguyen.identity.configuration.AuthenticationRequestInterceptor;
import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.ProfileCreationRequest;
import com.xuannguyen.identity.dto.response.UserProfileResponse;

import java.util.List;

@FeignClient(
        name = "profile-service",
        url = "${app.services.profile}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ProfileClient {
    @PostMapping(value = "/profiles/intenal/users", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<ProfileResponse> createProfile (@RequestBody ProfileCreationRequest request);

    @GetMapping(value ="/profiles/users/", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<List<ProfileResponse>>getAllProfiles();

}
