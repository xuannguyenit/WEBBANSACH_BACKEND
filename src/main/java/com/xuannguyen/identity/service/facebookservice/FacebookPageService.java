package com.xuannguyen.identity.service.facebookservice;

import com.xuannguyen.identity.dto.request.facebook.FacebookPageRequest;
import com.xuannguyen.identity.dto.request.facebook.FacebookPostProductsRequest;
import com.xuannguyen.identity.dto.response.facebook.FacebookPageResponse;
import com.xuannguyen.identity.dto.response.facebook.FacebookPostProductResponse;

import java.util.List;

public interface FacebookPageService {
    FacebookPageResponse saveFacebookPage(FacebookPageRequest request);

    List<FacebookPageResponse> getMyFacebookPages();

    void deleteFacebookPage(String id);

    List<FacebookPostProductResponse> postProductsToFacebookPage(
            FacebookPostProductsRequest request
    );
}
