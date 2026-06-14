package com.xuannguyen.identity.service.categoryservice.impl;


import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.categoryrequest.CategoryRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.UserResponse;
import com.xuannguyen.identity.dto.response.categoryresponse.CategoryResponse;
import com.xuannguyen.identity.dto.response.imageresponse.ImageResponse;
import com.xuannguyen.identity.entity.Category;
import com.xuannguyen.identity.entity.Image;
import com.xuannguyen.identity.entity.User;
import com.xuannguyen.identity.exception.AppException;
import com.xuannguyen.identity.exception.ErrorCode;
import com.xuannguyen.identity.repository.CategoryRepository;
import com.xuannguyen.identity.repository.ImageRepository;
import com.xuannguyen.identity.repository.UserRepository;
import com.xuannguyen.identity.service.categoryservice.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageRepository imageRepository;


    @Override
    public ApiResponse<Void> save(CategoryRequest request) {
        // lây thông tin user từ authentication
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        // kiểm tra userId có tồn tại không
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // kiểm tra caregory đã tồn tại chưa
        if (categoryRepository.existsByNameAndUser(request.getName(), currentUser)) {
            return ApiResponse.<Void>builder()
                    .code(200)
                    .message(ErrorCode.CATEGORY_BY_USER_EXISTED.getMessage())
                    .build(); // user đang có category này rồi
        }
        // kiêm tra ảnh đại diện có hợp lệ không
        if (!imageRepository.existsById(request.getAvatarId())){
            return ApiResponse.<Void>builder()
                    .code(200)
                    .message(ErrorCode.IMAGE_NOT_EXISTED.getMessage()) // ảnh không tồn tại
                    .build();
        }
        Image avatar = imageRepository.findById(request.getAvatarId())
                .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_EXISTED));
        Category category = Category.builder()
                .name(request.getName())
                .user(currentUser)
                .enable(true)
                .avatar(avatar)
                .build();
        categoryRepository.save(category); // lưu category vào database
        return ApiResponse.<Void>builder()
                .message("success")
                .code(200)
                .build();

    }

    @Override
    public ApiResponse<Void> update(String id, CategoryRequest request) {
        // Lấy thông tin user từ authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Kiểm tra user có tồn tại không
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Lấy thông tin category theo id
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        // Kiểm tra category có thuộc về user hiện tại không
        if (category.getUser() == null || !category.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Kiểm tra tên category có bị trùng với category khác của user không
        if (categoryRepository.existsByNameAndUserAndIdNot(request.getName(), currentUser, id)) {
            return ApiResponse.<Void>builder()
                    .code(200)
                    .message(ErrorCode.CATEGORY_BY_USER_EXISTED.getMessage())
                    .build();
        }

        // Kiểm tra ảnh đại diện nếu avatarId được truyền lên
        if (request.getAvatarId() != null && !request.getAvatarId().isBlank()) {
            Image avatar = imageRepository.findById(request.getAvatarId())
                    .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_EXISTED));
            category.setAvatar(avatar);
        }

        // Cập nhật category
        category.setName(request.getName());
        category.setEnable(request.isEnable());

        categoryRepository.save(category);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Update category successfully")
                .build();

    }

    @Override
    public ApiResponse<Void> changeStatus(String id) {
        // Lấy thông tin user hiện tại từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Lấy category theo id
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        // Kiểm tra user hiện tại có phải ADMIN không
        boolean isAdmin = currentUser.getRoles()
                .stream()
                .anyMatch(role -> "ADMIN".equals(role.getCode()));

        // Nếu không phải ADMIN thì chỉ chủ sở hữu category mới được đổi trạng thái
        if (!isAdmin) {
            if (category.getUser() == null || !category.getUser().getId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }

        // Đảo trạng thái category
        category.setEnable(!category.isEnable());

        categoryRepository.save(category);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Change category status successfully")
                .build();
    }

    // phương thức này dùng trong trang admin và admin nhà phân phối để kiểm tra category
    @Override
    public ApiResponse<CategoryResponse> getById(String id) {
        // Lấy thông tin user hiện tại từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Lấy category theo id
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        // Kiểm tra user hiện tại có phải ADMIN không
        boolean isAdmin = currentUser.getRoles()
                .stream()
                .anyMatch(role -> "ADMIN".equals(role.getCode()));
        // lấy thông tin user từ category
        User categoryUser = category.getUser();
        // Nếu không phải ADMIN thì chỉ được xem category của chính mình
        if (!isAdmin) {
            if (category.getUser() == null || !category.getUser().getId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }

        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .avatar(toImageResponse(category.getAvatar()))
                .user(toUserResponse(category))
                .enable(category.isEnable())
                .build();

        return ApiResponse.<CategoryResponse>builder()
                .code(200)
                .message("Get category successfully")
                .result(categoryResponse)
                .build();
    }
    @Override
    public ApiResponse<PageResponse<CategoryResponse>> getAll(int page, int size) {
        if (page < 1) {
            page = 1;
        }

        if (size < 1) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        List<CategoryResponse> categoryResponses = categoryPage.getContent()
                .stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .avatar(toImageResponse(category.getAvatar()))
                        .user(toUserResponse(category))
                        .enable(category.isEnable())
                        .build())
                .toList();

        PageResponse<CategoryResponse> pageResponse = PageResponse.<CategoryResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(categoryPage.getTotalPages())
                .totalElements(categoryPage.getTotalElements())
                .data(categoryResponses)
                .build();

        return ApiResponse.<PageResponse<CategoryResponse>>builder()
                .code(200)
                .message("Get categories successfully")
                .result(pageResponse)
                .build();
    }
    private UserResponse toUserResponse(Category category) {
        User user = category.getUser();
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
                .url("/images/" + image.getId() + "/data")
                .build();
    }
    @Override
    public ApiResponse<PageResponse<CategoryResponse>> getByUserId(Long userId, int page, int size) {
        // Validate page, size
        if (page < 1) {
            page = 1;
        }

        if (size < 1) {
            size = 10;
        }

        // Lấy thông tin user hiện tại từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Kiểm tra userId truyền vào có tồn tại không
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Kiểm tra currentUser có phải ADMIN không
        boolean isAdmin = currentUser.getRoles()
                .stream()
                .anyMatch(role -> "ADMIN".equals(role.getCode()));

        // Nếu không phải ADMIN thì chỉ được xem category của chính mình
        if (!isAdmin && !currentUser.getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Category> categoryPage = categoryRepository.findByUser(targetUser, pageable);

        List<CategoryResponse> categoryResponses = categoryPage.getContent()
                .stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .avatar(toImageResponse(category.getAvatar()))
                        .user(toUserResponse(category))
                        .enable(category.isEnable())
                        .build())
                .toList();

        PageResponse<CategoryResponse> pageResponse = PageResponse.<CategoryResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(categoryPage.getTotalPages())
                .totalElements(categoryPage.getTotalElements())
                .data(categoryResponses)
                .build();

        return ApiResponse.<PageResponse<CategoryResponse>>builder()
                .code(200)
                .message("Get categories by user successfully")
                .result(pageResponse)
                .build();
    }
}
