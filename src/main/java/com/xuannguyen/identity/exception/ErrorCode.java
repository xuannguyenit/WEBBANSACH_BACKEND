package com.xuannguyen.identity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "Tên đăng nhập đã tồn tại", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1009, "Invalid email address", HttpStatus.BAD_REQUEST),
    EMAIL_IS_REQUIRED(1009, "Email is required", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED (1010, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTED (1011, "Role not existed", HttpStatus.NOT_FOUND),
    CATEGORY_BY_USER_EXISTED(1012, "User đã tồn tại danh mục trên", HttpStatus.BAD_REQUEST),
    IMAGE_NOT_EXISTED(1013, "Ảnh đại diện không tồn tại", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_EXISTED(1014, "Category not existed", HttpStatus.NOT_FOUND),
    FILE_NOT_UPLOAD (1015, "File upload failed", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_TOO_LARGE(1016, "File size exceeds the maximum allowed size", HttpStatus.BAD_REQUEST),
    FILE_NOT_SUPPORT(1017, "File type is not supported", HttpStatus.BAD_REQUEST),
    INVALID_FILE(1018, "Invalid file", HttpStatus.BAD_REQUEST),
    BRAND_NAME_ALREADY_EXISTSED(1019, "Brand name already exists", HttpStatus.BAD_REQUEST),
    BRAND_NOT_EXISTED(404, "Brand không tồn tại", HttpStatus.NOT_FOUND),
    INVALID_BRAND_NAME(400, "Tên brand không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_CATEGORY_NAME(400, "Tên category không hợp lệ", HttpStatus.BAD_REQUEST),
    POST_NOT_EXISTED(404, "Bài viết không tồn tại", HttpStatus.NOT_FOUND),
    POST_SLUG_ALREADY_EXISTED(400, "Slug bài viết đã tồn tại", HttpStatus.BAD_REQUEST),
    INVALID_POST_TITLE(400, "Tiêu đề bài viết không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_POST_SLUG(400, "Slug bài viết không hợp lệ", HttpStatus.BAD_REQUEST);
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
