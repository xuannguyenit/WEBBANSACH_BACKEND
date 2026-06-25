package com.xuannguyen.identity.service.productartwork.impl;

import com.xuannguyen.identity.dto.request.productartwork.ProductArtworkFullRequest;
import com.xuannguyen.identity.dto.request.productartwork.ProductArtworkRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.productartwork.ProductArtworkResponse;
import com.xuannguyen.identity.entity.*;
import com.xuannguyen.identity.exception.AppException;
import com.xuannguyen.identity.exception.ErrorCode;
import com.xuannguyen.identity.repository.*;
import com.xuannguyen.identity.service.productartwork.ProductArtworkService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductArtworkServiceImpl implements ProductArtworkService {

    ProductArtworkRepository productArtworkRepository;
    CategoryRepository categoryRepository;
    BrandRepository brandRepository;
    ImageRepository imageRepository;
    UserRepository userRepository;

    private static final long MAX_IMAGE_SIZE = 30 * 1024 * 1024;

    @Override
    @Transactional
    public ProductArtworkResponse createFullProductArtwork(
            ProductArtworkFullRequest request,
            MultipartFile thumbnailFile,
            List<MultipartFile> imageFiles
    ) {
        validateFullCreateRequest(request);

        User currentUser = getCurrentUser();

        validateDuplicateFullArtwork(request, currentUser);

        Category category = validateCategory(request.getCategoryName());

        Brand brand = validateBrand(request.getBrandName());

        Image thumbnail = saveThumbnailIfPresent(thumbnailFile);

        String slug = generateUniqueSlug(request.getName());

        ProductArtwork productArtwork = ProductArtwork.builder()
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
                .metaTitle(trimToNull(request.getMetaTitle()))
                .metaDescription(trimToNull(request.getMetaDescription()))
                .metaKeywords(trimToNull(request.getMetaKeywords()))
                .type(ProductType.ART_PRODUCT)
                .category(category)
                .brand(brand)
                .thumbnail(thumbnail)
                .user(currentUser)
                .artistName(request.getArtistName().trim())
                .artworkType(trimToNull(request.getArtworkType()))
                .material(trimToNull(request.getMaterial()))
                .dimensions(trimToNull(request.getDimensions()))
                .weight(trimToNull(request.getWeight()))
                .handmade(request.getHandmade() != null ? request.getHandmade() : false)
                .limitedEdition(request.getLimitedEdition() != null ? request.getLimitedEdition() : false)
                .editionNumber(trimToNull(request.getEditionNumber()))
                .origin(trimToNull(request.getOrigin()))
                .build();

        List<ProductImage> productImages = saveProductImagesIfPresent(
                imageFiles,
                productArtwork
        );

        productArtwork.setImages(productImages);

        ProductArtwork savedProductArtwork = productArtworkRepository.save(productArtwork);

        return toResponse(savedProductArtwork);
    }

    @Override
    @Transactional
    public ProductArtworkResponse createProductArtwork(ProductArtworkRequest request) {
        validateRequest(request);

        User currentUser = getCurrentUser();

        validateDuplicateArtwork(request, currentUser);

        Category category = validateCategory(request.getCategoryName());

        Brand brand = validateBrand(request.getBrandName());

        Image thumbnail = validateImage(request.getThumbnailName());

        String slug = generateUniqueSlug(request.getName());

        ProductArtwork productArtwork = ProductArtwork.builder()
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
                .metaTitle(trimToNull(request.getMetaTitle()))
                .metaDescription(trimToNull(request.getMetaDescription()))
                .metaKeywords(trimToNull(request.getMetaKeywords()))
                .type(ProductType.ART_PRODUCT)
                .category(category)
                .brand(brand)
                .thumbnail(thumbnail)
                .user(currentUser)
                .artistName(request.getArtistName().trim())
                .artworkType(trimToNull(request.getArtworkType()))
                .material(trimToNull(request.getMaterial()))
                .dimensions(trimToNull(request.getDimensions()))
                .weight(trimToNull(request.getWeight()))
                .handmade(request.getHandmade() != null ? request.getHandmade() : false)
                .limitedEdition(request.getLimitedEdition() != null ? request.getLimitedEdition() : false)
                .editionNumber(trimToNull(request.getEditionNumber()))
                .origin(trimToNull(request.getOrigin()))
                .build();

        List<ProductImage> productImages = validateListImage(
                request.getImageName(),
                productArtwork
        );

        productArtwork.setImages(productImages);

        ProductArtwork savedProductArtwork = productArtworkRepository.save(productArtwork);

        return toResponse(savedProductArtwork);
    }

    @Override
    public PageResponse<ProductArtworkResponse> getAllProductPagination(int page, int size) {
        if (page < 1) {
            throw new RuntimeException("Page phải lớn hơn hoặc bằng 1");
        }

        if (size < 1) {
            throw new RuntimeException("Size phải lớn hơn hoặc bằng 1");
        }

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<ProductArtwork> productArtworkPage = productArtworkRepository.findAll(pageable);

        List<ProductArtworkResponse> responses = productArtworkPage
                .getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return PageResponse.<ProductArtworkResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(productArtworkPage.getTotalPages())
                .totalElements(productArtworkPage.getTotalElements())
                .data(responses)
                .build();
    }

    @Override
    public ProductArtworkResponse getById(String id) {
        ProductArtwork productArtwork = productArtworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm nghệ thuật không tồn tại"));

        return toResponse(productArtwork);
    }

    @Override
    @Transactional
    public ProductArtworkResponse enable(String id) {
        ProductArtwork productArtwork = productArtworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm nghệ thuật không tồn tại"));

        productArtwork.setEnable(true);

        return toResponse(productArtworkRepository.save(productArtwork));
    }

    @Override
    @Transactional
    public ProductArtworkResponse disable(String id) {
        ProductArtwork productArtwork = productArtworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm nghệ thuật không tồn tại"));

        productArtwork.setEnable(false);

        return toResponse(productArtworkRepository.save(productArtwork));
    }

    @Override
    @Transactional
    public void delete(String id) {
        ProductArtwork productArtwork = productArtworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm nghệ thuật không tồn tại"));

        productArtworkRepository.delete(productArtwork);
    }

    private ProductArtworkResponse toResponse(ProductArtwork productArtwork) {
        List<String> listImage = new ArrayList<>();

        if (productArtwork.getImages() != null) {
            productArtwork.getImages().forEach(productImage -> {
                if (productImage.getImage() != null) {
                    listImage.add(productImage.getImage().getName());
                }
            });
        }

        return ProductArtworkResponse.builder()
                .id(productArtwork.getId())
                .name(productArtwork.getName())
                .slug(productArtwork.getSlug())
                .description(productArtwork.getDescription())
                .price(productArtwork.getPrice())
                .salePrice(productArtwork.getSalePrice())
                .quantity(productArtwork.getQuantity())
                .sku(productArtwork.getSku())
                .enable(productArtwork.getEnable())
                .featured(productArtwork.getFeatured())
                .viewCount(productArtwork.getViewCount())
                .digital(productArtwork.getDigital())
                .createDate(productArtwork.getCreateDate())
                .type(productArtwork.getType() != null ? productArtwork.getType().name() : null)
                .categoryId(productArtwork.getCategory() != null ? productArtwork.getCategory().getId() : null)
                .brandId(productArtwork.getBrand() != null ? productArtwork.getBrand().getId() : null)
                .thumbnailId(productArtwork.getThumbnail() != null ? productArtwork.getThumbnail().getId() : null)
                .userId(productArtwork.getUser() != null ? productArtwork.getUser().getId() : null)
                .listImage(listImage)
                .metaTitle(productArtwork.getMetaTitle())
                .metaDescription(productArtwork.getMetaDescription())
                .metaKeywords(productArtwork.getMetaKeywords())
                .artistName(productArtwork.getArtistName())
                .artworkType(productArtwork.getArtworkType())
                .material(productArtwork.getMaterial())
                .dimensions(productArtwork.getDimensions())
                .weight(productArtwork.getWeight())
                .handmade(productArtwork.getHandmade())
                .limitedEdition(productArtwork.getLimitedEdition())
                .editionNumber(productArtwork.getEditionNumber())
                .origin(productArtwork.getOrigin())
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

    private void validateFullCreateRequest(ProductArtworkFullRequest request) {
        if (request == null) {
            throw new RuntimeException("Request không được null");
        }

        validateCommonFields(
                request.getName(),
                request.getPrice(),
                request.getSalePrice(),
                request.getQuantity(),
                request.getSku(),
                request.getArtistName()
        );

        if (!isBlank(request.getCategoryName())) {
            validateCategory(request.getCategoryName());
        }

        if (!isBlank(request.getBrandName())) {
            validateBrand(request.getBrandName());
        }
    }

    private void validateRequest(ProductArtworkRequest request) {
        if (request == null) {
            throw new RuntimeException("Request không được null");
        }

        validateCommonFields(
                request.getName(),
                request.getPrice(),
                request.getSalePrice(),
                request.getQuantity(),
                request.getSku(),
                request.getArtistName()
        );

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

    private void validateCommonFields(
            String name,
            BigDecimal price,
            BigDecimal salePrice,
            Integer quantity,
            String sku,
            String artistName
    ) {
        if (isBlank(name)) {
            throw new RuntimeException("Tên sản phẩm nghệ thuật không được để trống");
        }

        if (name.trim().length() > 255) {
            throw new RuntimeException("Tên sản phẩm nghệ thuật không được vượt quá 255 ký tự");
        }

        if (price == null) {
            throw new RuntimeException("Giá sản phẩm nghệ thuật không được để trống");
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Giá sản phẩm nghệ thuật không được âm");
        }

        if (salePrice != null && salePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Giá khuyến mại không được âm");
        }

        if (salePrice != null && salePrice.compareTo(price) > 0) {
            throw new RuntimeException("Giá khuyến mại không được lớn hơn giá gốc");
        }

        if (quantity != null && quantity < 0) {
            throw new RuntimeException("Số lượng tồn kho không được âm");
        }

        if (isBlank(artistName)) {
            throw new RuntimeException("Tên nghệ nhân / tác giả không được để trống");
        }

        if (artistName.trim().length() > 255) {
            throw new RuntimeException("Tên nghệ nhân / tác giả không được vượt quá 255 ký tự");
        }

        if (!isBlank(sku)) {
            String trimmedSku = sku.trim();

            if (trimmedSku.length() > 100) {
                throw new RuntimeException("SKU không được vượt quá 100 ký tự");
            }

            if (productArtworkRepository.existsBySku(trimmedSku)) {
                throw new RuntimeException("SKU đã tồn tại");
            }
        }
    }

    private void validateDuplicateFullArtwork(
            ProductArtworkFullRequest request,
            User user
    ) {
        if (user == null || user.getId() == null) {
            throw new RuntimeException("Không xác định được user hiện tại");
        }

        boolean duplicated = productArtworkRepository.existsDuplicateArtwork(
                request.getName().trim(),
                request.getArtistName().trim(),
                user.getId()
        );

        if (duplicated) {
            throw new RuntimeException("Sản phẩm nghệ thuật đã tồn tại với cùng tên và nghệ nhân");
        }
    }

    private void validateDuplicateArtwork(
            ProductArtworkRequest request,
            User user
    ) {
        if (user == null || user.getId() == null) {
            throw new RuntimeException("Không xác định được user hiện tại");
        }

        boolean duplicated = productArtworkRepository.existsDuplicateArtwork(
                request.getName().trim(),
                request.getArtistName().trim(),
                user.getId()
        );

        if (duplicated) {
            throw new RuntimeException("Sản phẩm nghệ thuật đã tồn tại với cùng tên và nghệ nhân");
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
            ProductArtwork productArtwork
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
                    .product(productArtwork)
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
            ProductArtwork productArtwork
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
                        .product(productArtwork)
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

        while (productArtworkRepository.existsBySlug(slug)) {
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
