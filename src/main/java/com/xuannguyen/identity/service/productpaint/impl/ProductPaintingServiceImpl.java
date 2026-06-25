package com.xuannguyen.identity.service.productpaint.impl;

import com.xuannguyen.identity.dto.request.productpant.ProductPaintingFullRequest;
import com.xuannguyen.identity.dto.request.productpant.ProductPaintingRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.productpaint.ProductPaintingResponse;
import com.xuannguyen.identity.entity.*;
import com.xuannguyen.identity.exception.AppException;
import com.xuannguyen.identity.exception.ErrorCode;
import com.xuannguyen.identity.repository.*;
import com.xuannguyen.identity.service.productpaint.ProductPaintingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductPaintingServiceImpl implements ProductPaintingService {
    ProductPaintingRepository productPaintingRepository;
    CategoryRepository categoryRepository;
    BrandRepository brandRepository;
    ImageRepository imageRepository;
    UserRepository userRepository;

    private static final long MAX_IMAGE_SIZE = 30 * 1024 * 1024;

    @Override
    @Transactional
    public ProductPaintingResponse createFullProductPainting(
            ProductPaintingFullRequest request,
            MultipartFile thumbnailFile,
            List<MultipartFile> imageFiles
    ) {
        validateFullCreateRequest(request);

        User currentUser = getCurrentUser();

        validateDuplicateFullPainting(request, currentUser);

        Category category = validateCategory(request.getCategoryName());

        Brand brand = validateBrand(request.getBrandName());

        Image thumbnail = saveThumbnailIfPresent(thumbnailFile);

        String slug = generateUniqueSlug(request.getName());

        ProductPainting productPainting = ProductPainting.builder()
                .name(request.getName().trim())
                .slug(slug)
                .description(trimToNull(request.getDescription()))
                .price(request.getPrice())
                .salePrice(request.getSalePrice())
                .quantity(request.getQuantity() != null ? request.getQuantity() : 0)
                .sku(trimToNull(request.getSku()))
                .enable(request.getEnable() != null ? request.getEnable() : true)
                .featured(request.getFeatured() != null ? request.getFeatured() : false)
                .viewCount(0L)
                .digital(request.getDigital() != null ? request.getDigital() : false)
                .createDate(LocalDate.now())
                .metaTitle(trimToNull(request.getMetaTitle()))
                .metaDescription(trimToNull(request.getMetaDescription()))
                .metaKeywords(trimToNull(request.getMetaKeywords()))
                .type(ProductType.PAINTING)
                .category(category)
                .brand(brand)
                .thumbnail(thumbnail)
                .user(currentUser)
                .artistName(request.getArtistName().trim())
                .medium(trimToNull(request.getMedium()))
                .style(trimToNull(request.getStyle()))
                .dimensions(trimToNull(request.getDimensions()))
                .yearCreated(request.getYearCreated())
                .framed(request.getFramed() != null ? request.getFramed() : false)
                .original(request.getOriginal() != null ? request.getOriginal() : true)
                .certificateIncluded(request.getCertificateIncluded() != null ? request.getCertificateIncluded() : false)
                .build();

        List<ProductImage> productImages = saveProductImagesIfPresent(
                imageFiles,
                productPainting
        );

        productPainting.setImages(productImages);

        ProductPainting savedProductPainting = productPaintingRepository.save(productPainting);

        return toResponse(savedProductPainting);
    }

    @Override
    @Transactional
    public ProductPaintingResponse createProductPainting(ProductPaintingRequest request) {
        validateRequest(request);

        User currentUser = getCurrentUser();

        validateDuplicatePainting(request, currentUser);

        Category category = validateCategory(request.getCategoryName());

        Brand brand = validateBrand(request.getBrandName());

        Image thumbnail = validateImage(request.getThumbnailName());

        String slug = generateUniqueSlug(request.getName());

        ProductPainting productPainting = ProductPainting.builder()
                .name(request.getName().trim())
                .slug(slug)
                .description(trimToNull(request.getDescription()))
                .price(request.getPrice())
                .salePrice(request.getSalePrice())
                .quantity(request.getQuantity() != null ? request.getQuantity() : 0)
                .sku(trimToNull(request.getSku()))
                .enable(request.getEnable() != null ? request.getEnable() : true)
                .featured(request.getFeatured() != null ? request.getFeatured() : false)
                .viewCount(0L)
                .digital(false)
                .createDate(LocalDate.now())
                .metaTitle(trimToNull(request.getMetaTitle()))
                .metaDescription(trimToNull(request.getMetaDescription()))
                .metaKeywords(trimToNull(request.getMetaKeywords()))
                .type(ProductType.PAINTING)
                .category(category)
                .brand(brand)
                .thumbnail(thumbnail)
                .user(currentUser)
                .artistName(request.getArtistName().trim())
                .medium(trimToNull(request.getMedium()))
                .style(trimToNull(request.getStyle()))
                .dimensions(trimToNull(request.getDimensions()))
                .yearCreated(request.getYearCreated())
                .framed(request.getFramed() != null ? request.getFramed() : false)
                .original(request.getOriginal() != null ? request.getOriginal() : true)
                .certificateIncluded(request.getCertificateIncluded() != null ? request.getCertificateIncluded() : false)
                .build();

        List<ProductImage> productImages = validateListImage(
                request.getImageName(),
                productPainting
        );

        productPainting.setImages(productImages);

        ProductPainting savedProductPainting = productPaintingRepository.save(productPainting);

        return toResponse(savedProductPainting);
    }

    @Override
    public PageResponse<ProductPaintingResponse> getAllProductPagination(int page, int size) {
        if (page < 1) {
            throw new RuntimeException("Page phải lớn hơn hoặc bằng 1");
        }

        if (size < 1) {
            throw new RuntimeException("Size phải lớn hơn hoặc bằng 1");
        }

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<ProductPainting> productPaintingPage = productPaintingRepository.findAll(pageable);

        List<ProductPaintingResponse> responses = productPaintingPage
                .getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return PageResponse.<ProductPaintingResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(productPaintingPage.getTotalPages())
                .totalElements(productPaintingPage.getTotalElements())
                .data(responses)
                .build();
    }

    @Override
    public ProductPaintingResponse getById(String id) {
        ProductPainting productPainting = productPaintingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm tranh không tồn tại"));

        return toResponse(productPainting);
    }

    @Override
    @Transactional
    public ProductPaintingResponse enable(String id) {
        ProductPainting productPainting = productPaintingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm tranh không tồn tại"));

        productPainting.setEnable(true);

        return toResponse(productPaintingRepository.save(productPainting));
    }

    @Override
    @Transactional
    public ProductPaintingResponse disable(String id) {
        ProductPainting productPainting = productPaintingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm tranh không tồn tại"));

        productPainting.setEnable(false);

        return toResponse(productPaintingRepository.save(productPainting));
    }

    @Override
    @Transactional
    public void delete(String id) {
        ProductPainting productPainting = productPaintingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm tranh không tồn tại"));

        productPaintingRepository.delete(productPainting);
    }

    private ProductPaintingResponse toResponse(ProductPainting productPainting) {
        List<String> listImage = new ArrayList<>();

        if (productPainting.getImages() != null) {
            productPainting.getImages().forEach(productImage -> {
                if (productImage.getImage() != null) {
                    listImage.add(productImage.getImage().getName());
                }
            });
        }

        return ProductPaintingResponse.builder()
                .id(productPainting.getId())
                .name(productPainting.getName())
                .slug(productPainting.getSlug())
                .description(productPainting.getDescription())
                .price(productPainting.getPrice())
                .salePrice(productPainting.getSalePrice())
                .quantity(productPainting.getQuantity())
                .sku(productPainting.getSku())
                .enable(productPainting.getEnable())
                .featured(productPainting.getFeatured())
                .viewCount(productPainting.getViewCount())
                .digital(productPainting.getDigital())
                .createDate(productPainting.getCreateDate())
                .type(productPainting.getType() != null ? productPainting.getType().name() : null)
                .categoryId(productPainting.getCategory() != null ? productPainting.getCategory().getId() : null)
                .brandId(productPainting.getBrand() != null ? productPainting.getBrand().getId() : null)
                .thumbnailId(productPainting.getThumbnail() != null ? productPainting.getThumbnail().getId() : null)
                .userId(productPainting.getUser() != null ? productPainting.getUser().getId() : null)
                .listImage(listImage)
                .metaTitle(productPainting.getMetaTitle())
                .metaDescription(productPainting.getMetaDescription())
                .metaKeywords(productPainting.getMetaKeywords())
                .artistName(productPainting.getArtistName())
                .medium(productPainting.getMedium())
                .style(productPainting.getStyle())
                .dimensions(productPainting.getDimensions())
                .yearCreated(productPainting.getYearCreated())
                .framed(productPainting.getFramed())
                .original(productPainting.getOriginal())
                .certificateIncluded(productPainting.getCertificateIncluded())
                .build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    private void validateFullCreateRequest(ProductPaintingFullRequest request) {
        if (request == null) {
            throw new RuntimeException("Request không được null");
        }

        if (isBlank(request.getName())) {
            throw new RuntimeException("Tên tranh không được để trống");
        }

        if (request.getName().trim().length() > 255) {
            throw new RuntimeException("Tên tranh không được vượt quá 255 ký tự");
        }

        if (request.getPrice() == null) {
            throw new RuntimeException("Giá tranh không được để trống");
        }

        if (request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Giá tranh không được âm");
        }

        if (request.getSalePrice() != null
                && request.getSalePrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Giá khuyến mại không được âm");
        }

        if (request.getSalePrice() != null
                && request.getSalePrice().compareTo(request.getPrice()) > 0) {
            throw new RuntimeException("Giá khuyến mại không được lớn hơn giá gốc");
        }

        if (request.getQuantity() != null && request.getQuantity() < 0) {
            throw new RuntimeException("Số lượng tồn kho không được âm");
        }

        if (isBlank(request.getArtistName())) {
            throw new RuntimeException("Tên họa sĩ không được để trống");
        }

        if (request.getArtistName().trim().length() > 255) {
            throw new RuntimeException("Tên họa sĩ không được vượt quá 255 ký tự");
        }

        if (!isBlank(request.getSku())) {
            String sku = request.getSku().trim();

            if (sku.length() > 100) {
                throw new RuntimeException("SKU không được vượt quá 100 ký tự");
            }

            if (productPaintingRepository.existsBySku(sku)) {
                throw new RuntimeException("SKU đã tồn tại");
            }
        }

        if (request.getYearCreated() != null) {
            int currentYear = LocalDate.now().getYear();

            if (request.getYearCreated() < 0) {
                throw new RuntimeException("Năm sáng tác không hợp lệ");
            }

            if (request.getYearCreated() > currentYear) {
                throw new RuntimeException("Năm sáng tác không được lớn hơn năm hiện tại");
            }
        }

        if (!isBlank(request.getCategoryName())) {
            validateCategory(request.getCategoryName());
        }

        if (!isBlank(request.getBrandName())) {
            validateBrand(request.getBrandName());
        }
    }

    private void validateRequest(ProductPaintingRequest request) {
        if (request == null) {
            throw new RuntimeException("Request không được null");
        }

        if (isBlank(request.getName())) {
            throw new RuntimeException("Tên tranh không được để trống");
        }

        if (request.getName().trim().length() > 255) {
            throw new RuntimeException("Tên tranh không được vượt quá 255 ký tự");
        }

        if (request.getPrice() == null) {
            throw new RuntimeException("Giá tranh không được để trống");
        }

        if (request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Giá tranh không được âm");
        }

        if (request.getSalePrice() != null
                && request.getSalePrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Giá khuyến mại không được âm");
        }

        if (request.getSalePrice() != null
                && request.getSalePrice().compareTo(request.getPrice()) > 0) {
            throw new RuntimeException("Giá khuyến mại không được lớn hơn giá gốc");
        }

        if (request.getQuantity() != null && request.getQuantity() < 0) {
            throw new RuntimeException("Số lượng tồn kho không được âm");
        }

        if (isBlank(request.getArtistName())) {
            throw new RuntimeException("Tên họa sĩ không được để trống");
        }

        if (request.getArtistName().trim().length() > 255) {
            throw new RuntimeException("Tên họa sĩ không được vượt quá 255 ký tự");
        }

        if (!isBlank(request.getSku())) {
            String sku = request.getSku().trim();

            if (sku.length() > 100) {
                throw new RuntimeException("SKU không được vượt quá 100 ký tự");
            }

            if (productPaintingRepository.existsBySku(sku)) {
                throw new RuntimeException("SKU đã tồn tại");
            }
        }

        if (request.getYearCreated() != null) {
            int currentYear = LocalDate.now().getYear();

            if (request.getYearCreated() < 0) {
                throw new RuntimeException("Năm sáng tác không hợp lệ");
            }

            if (request.getYearCreated() > currentYear) {
                throw new RuntimeException("Năm sáng tác không được lớn hơn năm hiện tại");
            }
        }

        if (!isBlank(request.getCategoryName())) {
            validateCategory(request.getCategoryName());
        }

        if (!isBlank(request.getBrandName())) {
            validateBrand(request.getBrandName());
        }

        if (!isBlank(request.getThumbnailName())) {
            validateImage(request.getThumbnailName());
        }

        if (request.getImageName() != null) {
            for (String imageName : request.getImageName()) {
                if (isBlank(imageName)) {
                    throw new RuntimeException("Tên ảnh phụ không được để trống");
                }

                imageRepository.findByName(imageName.trim())
                        .orElseThrow(() -> new RuntimeException(
                                "Ảnh phụ không tồn tại: " + imageName
                        ));
            }
        }
    }

    private void validateDuplicateFullPainting(
            ProductPaintingFullRequest request,
            User user
    ) {
        if (user == null || user.getId() == null) {
            throw new RuntimeException("Không xác định được user hiện tại");
        }

        boolean duplicated = productPaintingRepository.existsDuplicatePainting(
                request.getName().trim(),
                request.getArtistName().trim(),
                user.getId()
        );

        if (duplicated) {
            throw new RuntimeException("Sản phẩm tranh đã tồn tại với cùng tên tranh và họa sĩ");
        }
    }

    private void validateDuplicatePainting(
            ProductPaintingRequest request,
            User user
    ) {
        if (user == null || user.getId() == null) {
            throw new RuntimeException("Không xác định được user hiện tại");
        }

        boolean duplicated = productPaintingRepository.existsDuplicatePainting(
                request.getName().trim(),
                request.getArtistName().trim(),
                user.getId()
        );

        if (duplicated) {
            throw new RuntimeException("Sản phẩm tranh đã tồn tại với cùng tên tranh và họa sĩ");
        }
    }

    private Category validateCategory(String name) {
        if (isBlank(name)) {
            return null;
        }

        return categoryRepository.findByName(name.trim())
                .orElseThrow(() -> new RuntimeException("Category không tồn tại: " + name));
    }

    private Brand validateBrand(String brandName) {
        if (isBlank(brandName)) {
            return null;
        }

        return brandRepository.findByName(brandName.trim())
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED));
    }

    private Image validateImage(String thumbnailName) {
        if (isBlank(thumbnailName)) {
            return null;
        }

        return imageRepository.findByName(thumbnailName.trim())
                .orElseThrow(() -> new RuntimeException(
                        "Ảnh thumbnail không tồn tại: " + thumbnailName
                ));
    }

    private List<ProductImage> validateListImage(
            List<String> imageNames,
            ProductPainting productPainting
    ) {
        List<ProductImage> productImages = new ArrayList<>();

        if (imageNames == null || imageNames.isEmpty()) {
            return productImages;
        }

        int sortOrder = 1;

        for (String imageName : imageNames) {
            Image image = imageRepository.findByName(imageName.trim())
                    .orElseThrow(() -> new RuntimeException(
                            "Ảnh phụ không tồn tại: " + imageName
                    ));

            ProductImage productImage = ProductImage.builder()
                    .product(productPainting)
                    .image(image)
                    .sortOrder(sortOrder++)
                    .caption(null)
                    .build();

            productImages.add(productImage);
        }

        return productImages;
    }

    private Image saveThumbnailIfPresent(MultipartFile thumbnailFile) {
        if (thumbnailFile == null || thumbnailFile.isEmpty()) {
            return null;
        }

        validateImageFile(thumbnailFile);

        try {
            String fileName = normalizeFileName(thumbnailFile.getOriginalFilename());

            Image image = Image.builder()
                    .name(fileName)
                    .type(getImageExtension(fileName))
                    .size(thumbnailFile.getSize())
                    .data(thumbnailFile.getBytes())
                    .build();

            return imageRepository.save(image);
        } catch (Exception exception) {
            throw new RuntimeException("Upload thumbnail thất bại: " + exception.getMessage());
        }
    }

    private List<ProductImage> saveProductImagesIfPresent(
            List<MultipartFile> imageFiles,
            ProductPainting productPainting
    ) {
        List<ProductImage> productImages = new ArrayList<>();

        if (imageFiles == null || imageFiles.isEmpty()) {
            return productImages;
        }

        int sortOrder = 1;

        for (MultipartFile imageFile : imageFiles) {
            if (imageFile == null || imageFile.isEmpty()) {
                continue;
            }

            validateImageFile(imageFile);

            try {
                String fileName = normalizeFileName(imageFile.getOriginalFilename());

                Image savedImage = Image.builder()
                        .name(fileName)
                        .type(getImageExtension(fileName))
                        .size(imageFile.getSize())
                        .data(imageFile.getBytes())
                        .build();

                savedImage = imageRepository.save(savedImage);

                ProductImage productImage = ProductImage.builder()
                        .product(productPainting)
                        .image(savedImage)
                        .sortOrder(sortOrder++)
                        .caption(null)
                        .build();

                productImages.add(productImage);
            } catch (Exception exception) {
                throw new RuntimeException("Upload ảnh phụ thất bại: " + exception.getMessage());
            }
        }

        return productImages;
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Ảnh không được để trống");
        }

        String fileName = normalizeFileName(file.getOriginalFilename());
        String extension = getImageExtension(fileName);

        List<String> allowedExtensions = List.of(
                "png", "jpg", "jpeg", "gif", "svg"
        );

        if (!allowedExtensions.contains(extension)) {
            throw new RuntimeException("Chỉ hỗ trợ ảnh PNG, JPG, JPEG, GIF, SVG");
        }

        if (file.getSize() <= 0) {
            throw new RuntimeException("Ảnh không hợp lệ");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new RuntimeException("Ảnh không được vượt quá 30MB");
        }

        String contentType = file.getContentType();

        if (isBlank(contentType)) {
            throw new RuntimeException("Không xác định được loại ảnh");
        }

        boolean validContentType = contentType.equals("image/png")
                || contentType.equals("image/jpeg")
                || contentType.equals("image/gif")
                || contentType.equals("image/svg+xml");

        if (!validContentType) {
            throw new RuntimeException("Loại ảnh không hợp lệ");
        }
    }

    private String getImageExtension(String fileName) {
        if (isBlank(fileName) || !fileName.contains(".")) {
            throw new RuntimeException("Tên ảnh không hợp lệ");
        }

        return fileName
                .substring(fileName.lastIndexOf(".") + 1)
                .toLowerCase();
    }

    private String normalizeFileName(String fileName) {
        if (isBlank(fileName)) {
            throw new RuntimeException("Tên file không được để trống");
        }

        String normalized = fileName.trim();

        if (normalized.contains("..")
                || normalized.contains("/")
                || normalized.contains("\\")) {
            throw new RuntimeException("Tên file không hợp lệ");
        }

        return normalized;
    }

    private String generateUniqueSlug(String name) {
        String baseSlug = toSlug(name);
        String slug = baseSlug;

        int index = 1;

        while (productPaintingRepository.existsBySlug(slug)) {
            index++;
            slug = baseSlug + "-" + index;
        }

        return slug;
    }

    private String toSlug(String input) {
        if (isBlank(input)) {
            throw new RuntimeException("Không thể tạo slug từ tên rỗng");
        }

        String normalized = Normalizer.normalize(input.trim(), Normalizer.Form.NFD);

        String withoutAccent = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                .matcher(normalized)
                .replaceAll("");

        return withoutAccent
                .toLowerCase(Locale.ROOT)
                .replace("đ", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String trimToNull(String value) {
        if (isBlank(value)) {
            return null;
        }

        return value.trim();
    }
}

