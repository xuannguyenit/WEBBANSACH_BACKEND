package com.xuannguyen.identity.service.facebookservice.impl;

import com.xuannguyen.identity.dto.request.facebook.FacebookPageRequest;
import com.xuannguyen.identity.dto.request.facebook.FacebookPostProductsRequest;
import com.xuannguyen.identity.dto.response.facebook.FacebookPageResponse;
import com.xuannguyen.identity.dto.response.facebook.FacebookPostProductResponse;
import com.xuannguyen.identity.entity.FacebookPage;
import com.xuannguyen.identity.entity.FacebookProductPost;
import com.xuannguyen.identity.entity.Product;
import com.xuannguyen.identity.entity.User;
import com.xuannguyen.identity.exception.AppException;
import com.xuannguyen.identity.exception.ErrorCode;
import com.xuannguyen.identity.repository.FacebookPageRepository;
import com.xuannguyen.identity.repository.FacebookProductPostRepository;
import com.xuannguyen.identity.repository.ProductRepository;
import com.xuannguyen.identity.repository.UserRepository;
import com.xuannguyen.identity.service.facebookservice.FacebookPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FacebookPageServiceImpl implements FacebookPageService {

    private final FacebookPageRepository facebookPageRepository;

    private final FacebookProductPostRepository facebookProductPostRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://graph.facebook.com/v21.0")
            .build();

    private static final String POST_SUCCESS = "SUCCESS";

    private static final String POST_FAILED = "FAILED";

    @Override
    @Transactional
    public FacebookPageResponse saveFacebookPage(FacebookPageRequest request) {
        User user = getCurrentUser();

        validateFacebookPageRequest(request);

        Optional<FacebookPage> existedPage =
                facebookPageRepository.findByUserIdAndActiveTrue(user.getId());

        if (Boolean.TRUE.equals(request.getActive()) && existedPage.isPresent()) {
            FacebookPage oldActivePage = existedPage.get();
            oldActivePage.setActive(false);
            oldActivePage.setUpdatedAt(LocalDateTime.now());
            facebookPageRepository.save(oldActivePage);
        }

        boolean existed = facebookPageRepository.existsByUserIdAndPageId(
                user.getId(),
                request.getPageId().trim()
        );

        if (existed) {
            throw new AppException(ErrorCode.FACEBOOK_PAGE_ALREADY_EXISTED);
        }

        FacebookPage facebookPage = FacebookPage.builder()
                .userId(user.getId())
                .pageId(request.getPageId().trim())
                .pageName(trimToNull(request.getPageName()))
                .pageAccessToken(request.getPageAccessToken().trim())
                .active(request.getActive() != null ? request.getActive() : true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        FacebookPage savedPage = facebookPageRepository.save(facebookPage);

        return toFacebookPageResponse(savedPage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacebookPageResponse> getMyFacebookPages() {
        User user = getCurrentUser();

        return facebookPageRepository.findAllByUserId(user.getId())
                .stream()
                .map(this::toFacebookPageResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteFacebookPage(String id) {
        User user = getCurrentUser();

        FacebookPage facebookPage = facebookPageRepository.findByIdAndUserId(
                        id,
                        user.getId()
                )
                .orElseThrow(() -> new AppException(ErrorCode.FACEBOOK_PAGE_NOT_EXISTED));

        facebookPageRepository.delete(facebookPage);
    }

    @Override
    @Transactional
    public List<FacebookPostProductResponse> postProductsToFacebookPage(
            FacebookPostProductsRequest request
    ) {
        User user = getCurrentUser();

        validatePostProductsRequest(request);

        FacebookPage facebookPage = resolveFacebookPageConfig(
                request.getFacebookPageConfigId(),
                user
        );

        List<FacebookPostProductResponse> responses = new ArrayList<>();

        for (String productId : request.getProductIds()) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

            FacebookPostProductResponse response = postSingleProductToFacebook(
                    facebookPage,
                    product,
                    user,
                    request.getWebsiteBaseUrl(),
                    request.getPublicBackendBaseUrl()
            );

            responses.add(response);
        }

        return responses;
    }

    private FacebookPostProductResponse postSingleProductToFacebook(
            FacebookPage facebookPage,
            Product product,
            User user,
            String websiteBaseUrl,
            String publicBackendBaseUrl
    ) {
        String message = buildProductPostMessage(product, websiteBaseUrl);

        String imageUrl = buildProductImageUrl(product, publicBackendBaseUrl);

        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("message", message);
            body.put("access_token", facebookPage.getPageAccessToken());

            /*
             * Nếu có ảnh chính thì đăng qua /{page-id}/photos.
             * Nếu không có ảnh thì đăng text/link qua /{page-id}/feed.
             */
            Map responseBody;

            if (!isBlank(imageUrl)) {
                body.put("url", imageUrl);

                responseBody = restClient.post()
                        .uri("/{pageId}/photos", facebookPage.getPageId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)
                        .retrieve()
                        .body(Map.class);
            } else {
                body.put("link", buildProductDetailUrl(product, websiteBaseUrl));

                responseBody = restClient.post()
                        .uri("/{pageId}/feed", facebookPage.getPageId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)
                        .retrieve()
                        .body(Map.class);
            }

            String facebookPostId = extractFacebookPostId(responseBody);

            FacebookProductPost log = FacebookProductPost.builder()
                    .facebookPageConfigId(facebookPage.getId())
                    .userId(user.getId())
                    .pageId(facebookPage.getPageId())
                    .productId(product.getId())
                    .facebookPostId(facebookPostId)
                    .status(POST_SUCCESS)
                    .message(message)
                    .postedAt(LocalDateTime.now())
                    .build();

            facebookProductPostRepository.save(log);

            return FacebookPostProductResponse.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .facebookPostId(facebookPostId)
                    .status(POST_SUCCESS)
                    .message("Đăng sản phẩm lên fanpage thành công.")
                    .build();

        } catch (Exception exception) {
            FacebookProductPost log = FacebookProductPost.builder()
                    .facebookPageConfigId(facebookPage.getId())
                    .userId(user.getId())
                    .pageId(facebookPage.getPageId())
                    .productId(product.getId())
                    .status(POST_FAILED)
                    .message(message)
                    .errorMessage(exception.getMessage())
                    .postedAt(LocalDateTime.now())
                    .build();

            facebookProductPostRepository.save(log);

            return FacebookPostProductResponse.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .status(POST_FAILED)
                    .message("Đăng sản phẩm lên fanpage thất bại.")
                    .errorMessage(exception.getMessage())
                    .build();
        }
    }

    private String buildProductPostMessage(Product product, String websiteBaseUrl) {
        String productUrl = buildProductDetailUrl(product, websiteBaseUrl);

        BigDecimal price = product.getSalePrice() != null
                && product.getSalePrice().compareTo(BigDecimal.ZERO) > 0
                ? product.getSalePrice()
                : product.getPrice();

        StringBuilder message = new StringBuilder();

        message.append("📚 ").append(product.getName()).append("\n\n");

        if (!isBlank(product.getDescription())) {
            message.append(product.getDescription()).append("\n\n");
        }

        if (price != null) {
            message.append("💰 Giá: ").append(price).append(" VND\n");
        }

        message.append("🔗 Xem chi tiết: ").append(productUrl);

        return message.toString();
    }

    private String buildProductDetailUrl(Product product, String websiteBaseUrl) {
        String baseUrl = removeTrailingSlash(websiteBaseUrl);

        if (!isBlank(product.getSlug())) {
            return baseUrl + "/products/" + product.getSlug();
        }

        return baseUrl + "/products/" + product.getId();
    }

    private String buildProductImageUrl(Product product, String publicBackendBaseUrl) {
        if (product.getThumbnail() == null || isBlank(product.getThumbnail().getId())) {
            return null;
        }

        String baseUrl = removeTrailingSlash(publicBackendBaseUrl);

        return baseUrl + "/images/" + product.getThumbnail().getId() + "/view";
    }

    private String extractFacebookPostId(Map responseBody) {
        if (responseBody == null || responseBody.isEmpty()) {
            return null;
        }

        Object postId = responseBody.get("post_id");

        if (postId != null) {
            return postId.toString();
        }

        Object id = responseBody.get("id");

        if (id != null) {
            return id.toString();
        }

        return null;
    }

    private FacebookPage resolveFacebookPageConfig(
            String facebookPageConfigId,
            User user
    ) {
        if (!isBlank(facebookPageConfigId)) {
            return facebookPageRepository.findByIdAndUserId(
                            facebookPageConfigId,
                            user.getId()
                    )
                    .orElseThrow(() -> new AppException(ErrorCode.FACEBOOK_PAGE_NOT_EXISTED));
        }

        return facebookPageRepository.findByUserIdAndActiveTrue(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.FACEBOOK_PAGE_NOT_EXISTED));
    }

    private void validateFacebookPageRequest(FacebookPageRequest request) {
        if (request == null) {
            throw new AppException(ErrorCode.FACEBOOK_PAGE_NOT_EXISTED);
        }

        if (isBlank(request.getPageId())) {
            throw new AppException(ErrorCode.FACEBOOK_PAGE_ID_REQUIRED);
        }

        if (isBlank(request.getPageAccessToken())) {
            throw new AppException(ErrorCode.FACEBOOK_PAGE_TOKEN_REQUIRED);
        }
    }

    private void validatePostProductsRequest(FacebookPostProductsRequest request) {
        if (request == null || request.getProductIds() == null || request.getProductIds().isEmpty()) {
            throw new AppException(ErrorCode.FACEBOOK_PRODUCT_LIST_EMPTY);
        }

        if (isBlank(request.getWebsiteBaseUrl())) {
            throw new AppException(ErrorCode.WEBSITE_BASE_URL_REQUIRED);
        }

        if (isBlank(request.getPublicBackendBaseUrl())) {
            throw new AppException(ErrorCode.PUBLIC_BACKEND_BASE_URL_REQUIRED);
        }
    }

    private FacebookPageResponse toFacebookPageResponse(FacebookPage facebookPage) {
        return FacebookPageResponse.builder()
                .id(facebookPage.getId())
                .userId(facebookPage.getUserId())
                .pageId(facebookPage.getPageId())
                .pageName(facebookPage.getPageName())
                .active(facebookPage.getActive())
                .build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String username = authentication.getName();

        if (isBlank(username)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    private String trimToNull(String value) {
        if (isBlank(value)) {
            return null;
        }

        return value.trim();
    }

    private String removeTrailingSlash(String value) {
        if (value == null) {
            return null;
        }

        return value.replaceAll("/+$", "");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}