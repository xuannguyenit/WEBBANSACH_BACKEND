package com.xuannguyen.identity.service.postservice.impl;

import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.postrequest.PostCategoryResponse;
import com.xuannguyen.identity.dto.request.postrequest.PostImageRequest;
import com.xuannguyen.identity.dto.request.postrequest.PostRequest;
import com.xuannguyen.identity.dto.request.postrequest.PostSummaryResponse;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.UserResponse;
import com.xuannguyen.identity.dto.response.imageresponse.ImageResponse;
import com.xuannguyen.identity.dto.response.postresponse.PostImageResponse;
import com.xuannguyen.identity.dto.response.postresponse.PostResponse;
import com.xuannguyen.identity.entity.*;
import com.xuannguyen.identity.exception.AppException;
import com.xuannguyen.identity.exception.ErrorCode;
import com.xuannguyen.identity.repository.CategoryPostRepository;
import com.xuannguyen.identity.repository.ImageRepository;
import com.xuannguyen.identity.repository.PostRepository;
import com.xuannguyen.identity.repository.UserRepository;
import com.xuannguyen.identity.service.postservice.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private CategoryPostRepository categoryPostRepository;

    @Override
    public ApiResponse<Void> save(PostRequest request) {
        validatePostRequest(request);

        User currentUser = getCurrentUser();

        String title = request.getTitle().trim();
        String slug = buildSlug(request.getSlug(), title);

        if (postRepository.existsBySlug(slug)) {
            throw new AppException(ErrorCode.POST_SLUG_ALREADY_EXISTED);
        }

        Image thumbnail = null;
        if (request.getThumbnailId() != null && !request.getThumbnailId().isBlank()) {
            thumbnail = imageRepository.findById(request.getThumbnailId())
                    .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_EXISTED));
        }

        CategoryPost categoryPost = null;
        if (request.getCategoryPostId() != null) {
            categoryPost = categoryPostRepository.findById(request.getCategoryPostId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        }

        PostStatus status = request.getStatus() != null ? request.getStatus() : PostStatus.DRAFT;

        LocalDateTime publishedAt = request.getPublishedAt();
        if (PostStatus.PUBLISHED.equals(status) && publishedAt == null) {
            publishedAt = LocalDateTime.now();
        }

        Post post = Post.builder()
                .title(title)
                .slug(slug)
                .content(request.getContent())
                .description(request.getDescription())
                .author(
                        request.getAuthor() != null && !request.getAuthor().isBlank()
                                ? request.getAuthor().trim()
                                : currentUser.getUsername()
                )
                .status(status)
                .enable(request.getEnable() != null ? request.getEnable() : true)
                .featured(request.getFeatured() != null ? request.getFeatured() : false)
                .viewCount(0L)
                .publishedAt(publishedAt)
                .metaTitle(request.getMetaTitle())
                .metaDescription(request.getMetaDescription())
                .metaKeywords(request.getMetaKeywords())
                .user(currentUser)
                .categoryPost(categoryPost)
                .thumbnail(thumbnail)
                .images(new ArrayList<>())
                .build();

        List<PostImage> postImages = buildPostImages(post, request.getImages());
        post.getImages().addAll(postImages);

        postRepository.save(post);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Post created successfully")
                .build();
    }

    @Override
    public ApiResponse<Void> update(Long id, PostRequest request) {
        validatePostRequest(request);

        User currentUser = getCurrentUser();

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        checkOwnerOrAdmin(currentUser, post);

        String title = request.getTitle().trim();
        String slug = buildSlug(request.getSlug(), title);

        if (postRepository.existsBySlugAndIdNot(slug, id)) {
            throw new AppException(ErrorCode.POST_SLUG_ALREADY_EXISTED);
        }

        Image thumbnail = null;
        if (request.getThumbnailId() != null && !request.getThumbnailId().isBlank()) {
            thumbnail = imageRepository.findById(request.getThumbnailId())
                    .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_EXISTED));
        }

        CategoryPost categoryPost = null;
        if (request.getCategoryPostId() != null) {
            categoryPost = categoryPostRepository.findById(request.getCategoryPostId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        }

        PostStatus status = request.getStatus() != null ? request.getStatus() : post.getStatus();

        LocalDateTime publishedAt = request.getPublishedAt();
        if (PostStatus.PUBLISHED.equals(status) && publishedAt == null && post.getPublishedAt() == null) {
            publishedAt = LocalDateTime.now();
        } else if (publishedAt == null) {
            publishedAt = post.getPublishedAt();
        }

        post.setTitle(title);
        post.setSlug(slug);
        post.setContent(request.getContent());
        post.setDescription(request.getDescription());
        post.setAuthor(
                request.getAuthor() != null && !request.getAuthor().isBlank()
                        ? request.getAuthor().trim()
                        : post.getAuthor()
        );
        post.setStatus(status);
        post.setEnable(request.getEnable() != null ? request.getEnable() : post.getEnable());
        post.setFeatured(request.getFeatured() != null ? request.getFeatured() : post.getFeatured());
        post.setPublishedAt(publishedAt);
        post.setMetaTitle(request.getMetaTitle());
        post.setMetaDescription(request.getMetaDescription());
        post.setMetaKeywords(request.getMetaKeywords());
        post.setCategoryPost(categoryPost);

        if (request.getThumbnailId() != null && !request.getThumbnailId().isBlank()) {
            post.setThumbnail(thumbnail);
        }

        if (request.getImages() != null) {
            post.getImages().clear();
            post.getImages().addAll(buildPostImages(post, request.getImages()));
        }

        postRepository.save(post);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Post updated successfully")
                .build();
    }

    @Override
    public ApiResponse<Void> changeStatus(Long id) {
        User currentUser = getCurrentUser();

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        checkOwnerOrAdmin(currentUser, post);

        Boolean currentEnable = post.getEnable() != null ? post.getEnable() : true;
        post.setEnable(!currentEnable);

        postRepository.save(post);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Post status changed successfully")
                .build();
    }

    @Override
    public ApiResponse<Void> delete(Long id) {
        User currentUser = getCurrentUser();

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        checkOwnerOrAdmin(currentUser, post);

        postRepository.delete(post);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Post deleted successfully")
                .build();
    }

    @Override
    public ApiResponse<PostResponse> getById(Long id) {
        User currentUser = getCurrentUser();

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        checkOwnerOrAdmin(currentUser, post);

        return ApiResponse.<PostResponse>builder()
                .code(200)
                .message("Get post successfully")
                .result(toPostResponse(post))
                .build();
    }

    @Override
    public ApiResponse<PostResponse> getBySlug(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        return ApiResponse.<PostResponse>builder()
                .code(200)
                .message("Get post successfully")
                .result(toPostResponse(post))
                .build();
    }

    @Override
    public ApiResponse<PageResponse<PostSummaryResponse>> getAll(int page, int size) {
        if (page < 1) {
            page = 1;
        }

        if (size < 1) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Post> postPage = postRepository.findAll(pageable);

        List<PostSummaryResponse> postResponses = postPage.getContent()
                .stream()
                .map(this::toPostSummaryResponse)
                .toList();

        PageResponse<PostSummaryResponse> pageResponse = PageResponse.<PostSummaryResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(postPage.getTotalPages())
                .totalElements(postPage.getTotalElements())
                .data(postResponses)
                .build();

        return ApiResponse.<PageResponse<PostSummaryResponse>>builder()
                .code(200)
                .message("Get posts successfully")
                .result(pageResponse)
                .build();
    }

    @Override
    public ApiResponse<PageResponse<PostSummaryResponse>> getByUserId(Long userId, int page, int size) {
        if (page < 1) {
            page = 1;
        }

        if (size < 1) {
            size = 10;
        }

        User currentUser = getCurrentUser();

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isAdmin = isAdmin(currentUser);

        if (!isAdmin && !currentUser.getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Post> postPage = postRepository.findByUser(targetUser, pageable);

        List<PostSummaryResponse> postResponses = postPage.getContent()
                .stream()
                .map(this::toPostSummaryResponse)
                .toList();

        PageResponse<PostSummaryResponse> pageResponse = PageResponse.<PostSummaryResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(postPage.getTotalPages())
                .totalElements(postPage.getTotalElements())
                .data(postResponses)
                .build();

        return ApiResponse.<PageResponse<PostSummaryResponse>>builder()
                .code(200)
                .message("Get posts by user successfully")
                .result(pageResponse)
                .build();
    }

    @Override
    public ApiResponse<PageResponse<PostSummaryResponse>> getByCategoryPostId(Long categoryPostId, int page, int size) {
        if (page < 1) {
            page = 1;
        }

        if (size < 1) {
            size = 10;
        }

        CategoryPost categoryPost = categoryPostRepository.findById(categoryPostId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Post> postPage = postRepository.findByCategoryPost(categoryPost, pageable);

        List<PostSummaryResponse> postResponses = postPage.getContent()
                .stream()
                .map(this::toPostSummaryResponse)
                .toList();

        PageResponse<PostSummaryResponse> pageResponse = PageResponse.<PostSummaryResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(postPage.getTotalPages())
                .totalElements(postPage.getTotalElements())
                .data(postResponses)
                .build();

        return ApiResponse.<PageResponse<PostSummaryResponse>>builder()
                .code(200)
                .message("Get posts by category successfully")
                .result(pageResponse)
                .build();
    }

    @Override
    public ApiResponse<Void> increaseView(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        Long currentView = post.getViewCount() != null ? post.getViewCount() : 0L;
        post.setViewCount(currentView + 1);

        postRepository.save(post);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Increase post view successfully")
                .build();
    }

    private List<PostImage> buildPostImages(Post post, List<PostImageRequest> imageRequests) {
        List<PostImage> postImages = new ArrayList<>();

        if (imageRequests == null || imageRequests.isEmpty()) {
            return postImages;
        }

        for (PostImageRequest imageRequest : imageRequests) {
            if (imageRequest.getImageId() == null || imageRequest.getImageId().isBlank()) {
                continue;
            }

            Image image = imageRepository.findById(imageRequest.getImageId())
                    .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_EXISTED));

            PostImage postImage = PostImage.builder()
                    .post(post)
                    .image(image)
                    .sortOrder(imageRequest.getSortOrder())
                    .caption(imageRequest.getCaption())
                    .build();

            postImages.add(postImage);
        }

        return postImages;
    }

    private void validatePostRequest(PostRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new AppException(ErrorCode.INVALID_POST_TITLE);
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    private void checkOwnerOrAdmin(User currentUser, Post post) {
        boolean isAdmin = isAdmin(currentUser);

        if (!isAdmin) {
            if (post.getUser() == null || !post.getUser().getId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }
    }

    private boolean isAdmin(User user) {
        if (user.getRoles() == null) {
            return false;
        }

        return user.getRoles()
                .stream()
                .anyMatch(role -> "ADMIN".equals(role.getCode()));
    }

    private String buildSlug(String requestSlug, String title) {
        if (requestSlug != null && !requestSlug.isBlank()) {
            return slugify(requestSlug);
        }

        return slugify(title);
    }

    private String slugify(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

        String slug = pattern.matcher(normalized)
                .replaceAll("")
                .toLowerCase(Locale.ROOT)
                .replaceAll("đ", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        if (slug.isBlank()) {
            throw new AppException(ErrorCode.INVALID_POST_SLUG);
        }

        return slug;
    }

    private PostResponse toPostResponse(Post post) {
        if (post == null) {
            return null;
        }

        List<PostImageResponse> imageResponses = post.getImages() == null
                ? List.of()
                : post.getImages()
                .stream()
                .map(this::toPostImageResponse)
                .toList();

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .content(post.getContent())
                .description(post.getDescription())
                .author(post.getAuthor())
                .status(post.getStatus())
                .enable(post.getEnable())
                .featured(post.getFeatured())
                .viewCount(post.getViewCount())
                .publishedAt(post.getPublishedAt())
                .metaTitle(post.getMetaTitle())
                .metaDescription(post.getMetaDescription())
                .metaKeywords(post.getMetaKeywords())
                .user(toUserResponse(post.getUser()))
                .categoryPost(toPostCategoryResponse(post.getCategoryPost()))
                .thumbnail(toImageResponse(post.getThumbnail()))
                .images(imageResponses)
                .build();
    }

    private PostSummaryResponse toPostSummaryResponse(Post post) {
        if (post == null) {
            return null;
        }

        return PostSummaryResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .description(post.getDescription())
                .author(post.getAuthor())
                .status(post.getStatus())
                .enable(post.getEnable())
                .featured(post.getFeatured())
                .viewCount(post.getViewCount())
                .publishedAt(post.getPublishedAt())
                .user(toUserResponse(post.getUser()))
                .categoryPost(toPostCategoryResponse(post.getCategoryPost()))
                .thumbnail(toImageResponse(post.getThumbnail()))
                .build();
    }

    private PostImageResponse toPostImageResponse(PostImage postImage) {
        if (postImage == null) {
            return null;
        }

        return PostImageResponse.builder()
                .id(postImage.getId())
                .image(toImageResponse(postImage.getImage()))
                .sortOrder(postImage.getSortOrder())
                .caption(postImage.getCaption())
                .build();
    }

    private PostCategoryResponse toPostCategoryResponse(CategoryPost categoryPost) {
        if (categoryPost == null) {
            return null;
        }

        return PostCategoryResponse.builder()
                .id(categoryPost.getId())
                .name(categoryPost.getName())
                .image(toImageResponse(categoryPost.getImage()))
                .build();
    }

    private UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    private ImageResponse toImageResponse(Image image) {
        if (image == null) {
            return null;
        }

        return ImageResponse.builder()
                .id(image.getId())
                .name(image.getName())
                .type(image.getType())
                .size(image.getSize())
                .url("/images/" + image.getId() + "/view")
                .build();
    }
}
