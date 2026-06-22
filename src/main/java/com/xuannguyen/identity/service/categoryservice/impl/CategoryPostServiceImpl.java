package com.xuannguyen.identity.service.categoryservice.impl;

import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.categoryrequest.CategoryPostRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.UserResponse;
import com.xuannguyen.identity.dto.response.categoryresponse.CategoryPostResponse;
import com.xuannguyen.identity.dto.response.imageresponse.ImageResponse;
import com.xuannguyen.identity.entity.CategoryPost;
import com.xuannguyen.identity.entity.Image;
import com.xuannguyen.identity.entity.User;
import com.xuannguyen.identity.exception.AppException;
import com.xuannguyen.identity.exception.ErrorCode;
import com.xuannguyen.identity.repository.CategoryPostRepository;
import com.xuannguyen.identity.repository.ImageRepository;
import com.xuannguyen.identity.repository.UserRepository;
import com.xuannguyen.identity.service.categoryservice.CategoryPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryPostServiceImpl implements CategoryPostService {
    @Autowired
    private CategoryPostRepository categoryPostRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public ApiResponse<Void> save(CategoryPostRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new AppException(ErrorCode.INVALID_CATEGORY_NAME);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String categoryPostName = request.getName().trim();

        if (categoryPostRepository.existsByNameAndUser(categoryPostName, currentUser)) {
            throw new AppException(ErrorCode.CATEGORY_BY_USER_EXISTED);
        }

        Image image = null;

        if (request.getImageId() != null && !request.getImageId().isBlank()) {
            image = imageRepository.findById(request.getImageId())
                    .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_EXISTED));
        }

        CategoryPost categoryPost = CategoryPost.builder()
                .name(categoryPostName)
                .image(image)
                .user(currentUser)
                .build();

        categoryPostRepository.save(categoryPost);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Category post created successfully")
                .build();
    }

    @Override
    public ApiResponse<Void> update(Long id, CategoryPostRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new AppException(ErrorCode.INVALID_CATEGORY_NAME);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        CategoryPost categoryPost = categoryPostRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        boolean isAdmin = currentUser.getRoles()
                .stream()
                .anyMatch(role -> "ADMIN".equals(role.getCode()));

        if (!isAdmin) {
            if (categoryPost.getUser() == null || !categoryPost.getUser().getId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }

        String categoryPostName = request.getName().trim();

        if (categoryPostRepository.existsByNameAndUserAndIdNot(categoryPostName, currentUser, id)) {
            throw new AppException(ErrorCode.CATEGORY_BY_USER_EXISTED);
        }

        if (request.getImageId() != null && !request.getImageId().isBlank()) {
            Image image = imageRepository.findById(request.getImageId())
                    .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_EXISTED));
            categoryPost.setImage(image);
        }

        categoryPost.setName(categoryPostName);

        categoryPostRepository.save(categoryPost);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Category post updated successfully")
                .build();
    }

    @Override
    public ApiResponse<CategoryPostResponse> getById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        CategoryPost categoryPost = categoryPostRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        boolean isAdmin = currentUser.getRoles()
                .stream()
                .anyMatch(role -> "ADMIN".equals(role.getCode()));

        if (!isAdmin) {
            if (categoryPost.getUser() == null || !categoryPost.getUser().getId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }

        return ApiResponse.<CategoryPostResponse>builder()
                .code(200)
                .message("Get category post successfully")
                .result(toCategoryPostResponse(categoryPost))
                .build();
    }

    @Override
    public ApiResponse<PageResponse<CategoryPostResponse>> getAll(int page, int size) {
        if (page < 1) {
            page = 1;
        }

        if (size < 1) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<CategoryPost> categoryPostPage = categoryPostRepository.findAll(pageable);

        List<CategoryPostResponse> categoryPostResponses = categoryPostPage.getContent()
                .stream()
                .map(this::toCategoryPostResponse)
                .toList();

        PageResponse<CategoryPostResponse> pageResponse = PageResponse.<CategoryPostResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(categoryPostPage.getTotalPages())
                .totalElements(categoryPostPage.getTotalElements())
                .data(categoryPostResponses)
                .build();

        return ApiResponse.<PageResponse<CategoryPostResponse>>builder()
                .code(200)
                .message("Get category posts successfully")
                .result(pageResponse)
                .build();
    }

    @Override
    public ApiResponse<PageResponse<CategoryPostResponse>> getByUserId(Long userId, int page, int size) {
        if (page < 1) {
            page = 1;
        }

        if (size < 1) {
            size = 10;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isAdmin = currentUser.getRoles()
                .stream()
                .anyMatch(role -> "ADMIN".equals(role.getCode()));

        if (!isAdmin && !currentUser.getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<CategoryPost> categoryPostPage = categoryPostRepository.findByUser(targetUser, pageable);

        List<CategoryPostResponse> categoryPostResponses = categoryPostPage.getContent()
                .stream()
                .map(this::toCategoryPostResponse)
                .toList();

        PageResponse<CategoryPostResponse> pageResponse = PageResponse.<CategoryPostResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(categoryPostPage.getTotalPages())
                .totalElements(categoryPostPage.getTotalElements())
                .data(categoryPostResponses)
                .build();

        return ApiResponse.<PageResponse<CategoryPostResponse>>builder()
                .code(200)
                .message("Get category posts by user successfully")
                .result(pageResponse)
                .build();
    }

    private CategoryPostResponse toCategoryPostResponse(CategoryPost categoryPost) {
        if (categoryPost == null) {
            return null;
        }

        return CategoryPostResponse.builder()
                .id(categoryPost.getId())
                .name(categoryPost.getName())
                .image(toImageResponse(categoryPost.getImage()))
                .user(toUserResponse(categoryPost.getUser()))
                .build();
    }

    private UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .username(user.getUsername())
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
