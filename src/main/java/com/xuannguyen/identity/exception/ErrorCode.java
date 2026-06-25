package com.xuannguyen.identity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Đã xảy ra lỗi không xác định.", HttpStatus.INTERNAL_SERVER_ERROR),

    INVALID_KEY(1001, "Khóa không hợp lệ.", HttpStatus.BAD_REQUEST),

    USER_EXISTED(1002, "Tên đăng nhập đã tồn tại.", HttpStatus.BAD_REQUEST),

    USERNAME_INVALID(1003, "Tên đăng nhập phải có ít nhất {min} ký tự.", HttpStatus.BAD_REQUEST),

    INVALID_PASSWORD(1004, "Mật khẩu phải có ít nhất {min} ký tự.", HttpStatus.BAD_REQUEST),

    USER_NOT_EXISTED(1005, "Người dùng không tồn tại.", HttpStatus.NOT_FOUND),

    UNAUTHENTICATED(1006, "Bạn chưa đăng nhập.", HttpStatus.UNAUTHORIZED),

    UNAUTHORIZED(1007, "Bạn không có quyền thực hiện chức năng này.", HttpStatus.FORBIDDEN),

    INVALID_DOB(1008, "Tuổi phải lớn hơn hoặc bằng {min}.", HttpStatus.BAD_REQUEST),

    INVALID_EMAIL(1009, "Địa chỉ email không hợp lệ.", HttpStatus.BAD_REQUEST),

    EMAIL_IS_REQUIRED(1010, "Email là bắt buộc.", HttpStatus.BAD_REQUEST),

    EMAIL_EXISTED(1011, "Email đã tồn tại.", HttpStatus.BAD_REQUEST),

    ROLE_NOT_EXISTED(1012, "Vai trò không tồn tại.", HttpStatus.NOT_FOUND),

    CATEGORY_BY_USER_EXISTED(1013, "Danh mục này đã tồn tại trong tài khoản của bạn.", HttpStatus.BAD_REQUEST),

    IMAGE_NOT_EXISTED(1014, "Ảnh không tồn tại.", HttpStatus.NOT_FOUND),

    CATEGORY_NOT_EXISTED(1015, "Danh mục không tồn tại.", HttpStatus.NOT_FOUND),

    FILE_NOT_UPLOAD(1016, "Không thể tải tệp lên hệ thống.", HttpStatus.INTERNAL_SERVER_ERROR),

    FILE_TOO_LARGE(1017, "Kích thước tệp vượt quá giới hạn cho phép.", HttpStatus.BAD_REQUEST),

    FILE_NOT_SUPPORT(1018, "Định dạng tệp không được hỗ trợ.", HttpStatus.BAD_REQUEST),

    INVALID_FILE(1019, "Tệp không hợp lệ.", HttpStatus.BAD_REQUEST),

    BRAND_NAME_ALREADY_EXISTED(1020, "Tên thương hiệu đã tồn tại.", HttpStatus.BAD_REQUEST),

    BRAND_NOT_EXISTED(1021, "Thương hiệu không tồn tại.", HttpStatus.NOT_FOUND),

    INVALID_BRAND_NAME(1022, "Tên thương hiệu không hợp lệ.", HttpStatus.BAD_REQUEST),

    INVALID_CATEGORY_NAME(1023, "Tên danh mục không hợp lệ.", HttpStatus.BAD_REQUEST),

    POST_NOT_EXISTED(1024, "Bài viết không tồn tại.", HttpStatus.NOT_FOUND),

    POST_SLUG_ALREADY_EXISTED(1025, "Slug bài viết đã tồn tại.", HttpStatus.BAD_REQUEST),

    INVALID_POST_TITLE(1026, "Tiêu đề bài viết không hợp lệ.", HttpStatus.BAD_REQUEST),

    INVALID_POST_SLUG(1027, "Slug bài viết không hợp lệ.", HttpStatus.BAD_REQUEST),

    BOOK_DUPLICATION(
            1028,
            "Sách đã tồn tại với cùng tên, tác giả và số tập trong tài khoản hiện tại.",
            HttpStatus.BAD_REQUEST
    ),

    INSUFFICIENT_QUANTITY_OF_PRODUCTS(
            1029,
            "Số lượng sản phẩm trong kho không đủ.",
            HttpStatus.BAD_REQUEST
    ),
    PRODUCT_ID_REQUIRED(
            1030,
            "Product id không được để trống.",
            HttpStatus.BAD_REQUEST
    ),

    PRODUCT_NOT_EXISTED(
            1031,
            "Sản phẩm không tồn tại.",
            HttpStatus.NOT_FOUND
    ),

    PRODUCT_DISABLED(
            1032,
            "Sản phẩm đang bị tắt, không thể thêm vào giỏ hàng.",
            HttpStatus.BAD_REQUEST
    ),

    CART_NOT_EXISTED(
            1033,
            "Giỏ hàng không tồn tại.",
            HttpStatus.NOT_FOUND
    ),

    CART_ID_REQUIRED(
            1034,
            "Cart id không được để trống.",
            HttpStatus.BAD_REQUEST
    ),

    CART_ITEM_NOT_EXISTED(
            1035,
            "Sản phẩm không tồn tại trong giỏ hàng.",
            HttpStatus.NOT_FOUND
    ),

    INVALID_CART_ITEM_QUANTITY(
            1036,
            "Số lượng sản phẩm trong giỏ hàng không hợp lệ.",
            HttpStatus.BAD_REQUEST
    ),

    UPDATE_CART_ITEM_REQUEST_INVALID(
            1037,
            "Thông tin cập nhật giỏ hàng không hợp lệ.",
            HttpStatus.BAD_REQUEST
    ),

    CART_NOT_BELONG_TO_USER(
            1038,
            "Bạn không có quyền thao tác giỏ hàng này.",
            HttpStatus.FORBIDDEN
    ),

    ORDER_NOT_EXISTED(
            1039,
            "Đơn hàng không tồn tại.",
            HttpStatus.NOT_FOUND
    ),

    ORDER_DETAIL_NOT_EXISTED(
            1040,
            "Chi tiết đơn hàng không tồn tại.",
            HttpStatus.NOT_FOUND
    ),

    ORDER_ID_REQUIRED(
            1041,
            "Order id không được để trống.",
            HttpStatus.BAD_REQUEST
    ),

    ORDER_DETAIL_ID_REQUIRED(
            1042,
            "Order detail id không được để trống.",
            HttpStatus.BAD_REQUEST
    ),

    ORDER_DETAIL_REQUEST_INVALID(
            1043,
            "Thông tin chi tiết đơn hàng không hợp lệ.",
            HttpStatus.BAD_REQUEST
    ),

    PRODUCT_QUANTITY_INVALID(
            1044,
            "Số lượng sản phẩm không hợp lệ.",
            HttpStatus.BAD_REQUEST
    ),

    USER_ID_REQUIRED(
            1045,
            "User id không được để trống.",
            HttpStatus.BAD_REQUEST),
    ORDER_NOTE_EXITS (1046, "Không tồn tại đơn hàng", HttpStatus.BAD_REQUEST),



    CART_EMPTY(
            1046,
            "Giỏ hàng đang trống.",
            HttpStatus.BAD_REQUEST
    ),

    ORDER_ALREADY_COMPLETED(
            1047,
            "Đơn hàng đã hoàn thành.",
            HttpStatus.BAD_REQUEST
    ),
    PAYMENT_NOT_EXISTED(
            1048,
            "Giao dịch thanh toán không tồn tại.",
            HttpStatus.NOT_FOUND
    ),

    PAYMENT_SIGNATURE_INVALID(
            1049,
            "Chữ ký thanh toán không hợp lệ.",
            HttpStatus.BAD_REQUEST
    ),

    PAYMENT_AMOUNT_INVALID(
            1050,
            "Số tiền thanh toán không hợp lệ.",
            HttpStatus.BAD_REQUEST
    ),

    PAYMENT_ORDER_INVALID(
            1051,
            "Đơn hàng thanh toán không hợp lệ.",
            HttpStatus.BAD_REQUEST
    ),

    PAYMENT_FAILED(
            1052,
            "Thanh toán thất bại.",
            HttpStatus.BAD_REQUEST
    ),
    FACEBOOK_PAGE_NOT_EXISTED(
            1053,
            "Cấu hình fanpage không tồn tại.",
            HttpStatus.NOT_FOUND
    ),

    FACEBOOK_PAGE_ID_REQUIRED(
            1054,
            "Page id không được để trống.",
            HttpStatus.BAD_REQUEST
    ),

    FACEBOOK_PAGE_TOKEN_REQUIRED(
            1055,
            "Page access token không được để trống.",
            HttpStatus.BAD_REQUEST
    ),

    FACEBOOK_PAGE_ALREADY_EXISTED(
            1056,
            "Fanpage này đã được cấu hình trong tài khoản của bạn.",
            HttpStatus.BAD_REQUEST
    ),

    FACEBOOK_PRODUCT_LIST_EMPTY(
            1057,
            "Danh sách sản phẩm đăng lên fanpage không được để trống.",
            HttpStatus.BAD_REQUEST
    ),

    FACEBOOK_POST_FAILED(
            1058,
            "Đăng bài lên fanpage thất bại.",
            HttpStatus.BAD_REQUEST
    ),

    WEBSITE_BASE_URL_REQUIRED(
            1059,
            "Website base URL không được để trống.",
            HttpStatus.BAD_REQUEST
    ),

    PUBLIC_BACKEND_BASE_URL_REQUIRED(
            1060,
            "Public backend base URL không được để trống.",
            HttpStatus.BAD_REQUEST
    );


    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
