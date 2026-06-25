package com.xuannguyen.identity.service.productbook.impl;

import com.xuannguyen.identity.dto.request.productbook.ProductBookFullRequest;
import com.xuannguyen.identity.dto.request.productbook.ProductBookRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.productbook.ProductBookImportError;
import com.xuannguyen.identity.dto.response.productbook.ProductBookImportResponse;
import com.xuannguyen.identity.dto.response.productbook.ProductBookResponse;
import com.xuannguyen.identity.entity.*;
import com.xuannguyen.identity.exception.AppException;
import com.xuannguyen.identity.exception.ErrorCode;
import com.xuannguyen.identity.repository.*;
import com.xuannguyen.identity.service.productbook.ProductBookService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductBookServiceImpl implements ProductBookService {
    ProductBookRepository productBookRepository;
    CategoryRepository categoryRepository;
    BrandRepository brandRepository;
    ImageRepository imageRepository;
    FileResourceRepository fileResourceRepository;
    UserRepository userRepository;

    // Theme mới full thông tin cho sản phẩm
    @Override
    @Transactional
    public ProductBookResponse createFullProductBook(
            ProductBookFullRequest request,
            MultipartFile thumbnailFile,
            List<MultipartFile> imageFiles,
            MultipartFile pdfFile
    ) {
        validateFullCreateRequest(request);

        User currentUser = getCurrentUser();

        validateDuplicateFullBook(request, currentUser);

        Category category = validateCategory(request.getCategoryName());

        Brand brand = validateBrand(request.getBrandName());

        Image thumbnail = saveThumbnailIfPresent(thumbnailFile);

        FileResource savedPdfFile = saveBookFileIfPresent(pdfFile);

        String slug = generateUniqueSlug(request.getName());

        ProductBook productBook = ProductBook.builder()
                .name(request.getName().trim())
                .slug(slug)
                .description(trimToNull(request.getDescription()))
                .price(request.getPrice())
                .salePrice(request.getSalePrice())
                .quantity(request.getQuantity())
                .sku(trimToNull(request.getSku()))
                .enable(request.getEnable() != null ? request.getEnable() : true)
                .featured(request.getFeatured() != null ? request.getFeatured() : false)
                .viewCount(0L)
                .digital(request.getDigital() != null ? request.getDigital() : false)
                .metaTitle(trimToNull(request.getMetaTitle()))
                .metaDescription(trimToNull(request.getMetaDescription()))
                .metaKeywords(trimToNull(request.getMetaKeywords()))
                .type(ProductType.BOOK)
                .category(category)
                .brand(brand)
                .thumbnail(thumbnail)
                .user(currentUser)
                .isbn(trimToNull(request.getIsbn()))
                .volumeNumber(request.getVolumeNumber())
                .bookAuthor(request.getBookAuthor().trim())
                .publisher(trimToNull(request.getPublisher()))
                .pageCount(request.getPageCount())
                .publicationDate(request.getPublicationDate())
                .language(trimToNull(request.getLanguage()))
                .bookFormat(trimToNull(request.getBookFormat()))
                .pdfFile(savedPdfFile)
                .build();

        List<ProductImage> productImages = saveProductImagesIfPresent(
                imageFiles,
                productBook
        );

        productBook.setImages(productImages);

        ProductBook savedProductBook = productBookRepository.save(productBook);

        return toResponse(savedProductBook);
    }

    private User getCurrentUser() {
        Authentication context = SecurityContextHolder.getContext().getAuthentication();
        String name = context.getName();
        User curentUser = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));
        return curentUser;
    }

    private void validateFullCreateRequest(ProductBookFullRequest request) {
        if (request == null) {
            throw new RuntimeException("Request không được null");
        }

        if (isBlank(request.getName())) {
            throw new RuntimeException("Tên sách không được để trống");
        }

        if (request.getName().trim().length() > 255) {
            throw new RuntimeException("Tên sách không được vượt quá 255 ký tự");
        }

        if (request.getPrice() == null) {
            throw new RuntimeException("Giá sách không được để trống");
        }

        if (request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Giá sách không được âm");
        }

        if (request.getSalePrice() != null
                && request.getSalePrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Giá khuyến mại không được âm");
        }

        if (request.getSalePrice() != null
                && request.getSalePrice().compareTo(request.getPrice()) > 0) {
            throw new RuntimeException("Giá khuyến mại không được lớn hơn giá gốc");
        }

        if (request.getQuantity() < 0) {
            throw new RuntimeException("Số lượng tồn kho không được âm");
        }

        if (isBlank(request.getBookAuthor())) {
            throw new RuntimeException("Tác giả sách không được để trống");
        }

        if (request.getBookAuthor().trim().length() > 255) {
            throw new RuntimeException("Tên tác giả không được vượt quá 255 ký tự");
        }

        if (request.getVolumeNumber() != null && request.getVolumeNumber() < 1) {
            throw new RuntimeException("Số tập của sách phải lớn hơn hoặc bằng 1");
        }

        if (!isBlank(request.getSku())) {
            String sku = request.getSku().trim();

            if (sku.length() > 100) {
                throw new RuntimeException("SKU không được vượt quá 100 ký tự");
            }

            if (productBookRepository.existsBySku(sku)) {
                throw new RuntimeException("SKU đã tồn tại");
            }
        }

        if (!isBlank(request.getIsbn())) {
            String isbn = request.getIsbn().trim();

            if (isbn.length() > 50) {
                throw new RuntimeException("ISBN không được vượt quá 50 ký tự");
            }

            if (productBookRepository.existsByIsbn(isbn)) {
                throw new RuntimeException("ISBN đã tồn tại");
            }
        }

        if (request.getPageCount() != null && request.getPageCount() < 1) {
            throw new RuntimeException("Số trang phải lớn hơn hoặc bằng 1");
        }

        if (!isBlank(request.getCategoryName())) {
            validateCategory(request.getCategoryName());
        }

        if (!isBlank(request.getBrandName())) {
            validateBrand(request.getBrandName());
        }
    }

    private void validateDuplicateFullBook(
            ProductBookFullRequest request,
            User user
    ) {
        if (user == null || user.getId() == null) {
            throw new RuntimeException("Không xác định được user hiện tại");
        }

        boolean duplicated = productBookRepository.existsDuplicateBook(
                request.getName().trim(),
                request.getBookAuthor().trim(),
                request.getVolumeNumber(),
                user.getId()
        );

        if (duplicated) {
            throw new AppException(ErrorCode.BOOK_DUPLICATION);
        }
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
        } catch (Exception e) {
            throw new RuntimeException("Upload thumbnail thất bại: " + e.getMessage());
        }
    }

    private List<ProductImage> saveProductImagesIfPresent(
            List<MultipartFile> imageFiles,
            ProductBook productBook
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
                        .product(productBook)
                        .image(savedImage)
                        .sortOrder(sortOrder++)
                        .caption(null)
                        .build();

                productImages.add(productImage);
            } catch (Exception e) {
                throw new RuntimeException("Upload ảnh phụ thất bại: " + e.getMessage());
            }
        }

        return productImages;
    }

    private FileResource saveBookFileIfPresent(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateBookFile(file);

        try {
            String fileName = normalizeFileName(file.getOriginalFilename());

            FileResource fileResource = FileResource.builder()
                    .name(fileName)
                    .type(file.getContentType())
                    .size(file.getSize())
                    .data(file.getBytes())
                    .build();

            return fileResourceRepository.save(fileResource);
        } catch (Exception e) {
            throw new RuntimeException("Upload file sách thất bại: " + e.getMessage());
        }
    }

    private static final long MAX_IMAGE_SIZE = 30 * 1024 * 1024;

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

        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private static final long MAX_BOOK_FILE_SIZE = 50 * 1024 * 1024;

    private void validateBookFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File sách không được để trống");
        }

        String fileName = normalizeFileName(file.getOriginalFilename());

        validateBookFileExtension(fileName);
        validateBookFileContentType(file.getContentType());

        if (file.getSize() <= 0) {
            throw new RuntimeException("File sách không hợp lệ");
        }

        if (file.getSize() > MAX_BOOK_FILE_SIZE) {
            throw new RuntimeException("File sách không được vượt quá 50MB");
        }
    }

    private void validateBookFileExtension(String fileName) {
        String lowerName = fileName.toLowerCase();

        if (!lowerName.endsWith(".pdf")
                && !lowerName.endsWith(".doc")
                && !lowerName.endsWith(".docx")) {
            throw new RuntimeException("Chỉ hỗ trợ file PDF, DOC, DOCX");
        }
    }

    private void validateBookFileContentType(String contentType) {
        if (isBlank(contentType)) {
            throw new RuntimeException("Không xác định được loại file sách");
        }

        boolean valid = contentType.equals("application/pdf")
                || contentType.equals("application/msword")
                || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        if (!valid) {
            throw new RuntimeException("Loại file sách không hợp lệ. Chỉ chấp nhận PDF, DOC, DOCX");
        }
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

    //    Kết thúc thêm mới full thông tin
    @Override
    public ProductBookResponse createProductBook(ProductBookRequest request) {
        // lấy thông tin user đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = getCurrentUser();
        validateDuplicateBook(request, currentUser);
        Category category = validateCategory(request.getCategoryName());
        Brand brand = validateBrand(request.getBrandName());
        Image thumbnail = validateImage(request.getThumbnailName());
        FileResource file = validateFile(request.getPdfFileName());
        String slug = generateUniqueSlug(request.getName());
        validateRequest(request);
        ProductBook productBook = ProductBook.builder()
                .name(request.getName().trim())
                .slug(slug)
                .description(trimToNull(request.getDescription()))
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .sku(trimToNull(request.getSku()))
                .enable(request.getEnable() != null ? request.getEnable() : true)
                .featured(request.getFeatured() != null ? request.getFeatured() : false)
                .viewCount(0L)
                .digital(false)
                .type(ProductType.BOOK)
                .category(category)
                .brand(brand)
                .thumbnail(thumbnail)
                .user(currentUser)
                .isbn(trimToNull(request.getIsbn()))
                .volumeNumber(request.getVolumeNumber())
                .bookAuthor(request.getBookAuthor().trim())
                .publisher(trimToNull(request.getPublisher()))
                .pageCount(request.getPageCount())
                .language(trimToNull(request.getLanguage()))
                .pdfFile(file)
                .build();

        List<ProductImage> listImage = validateListImage(request.getImageName(), productBook);
        productBook.setImages(listImage);

        ProductBook savedProductBook = productBookRepository.save(productBook);
        return toResponse(savedProductBook);
    }

    private ProductBookResponse toResponse(ProductBook productBook) {
        List<String> listImage = new ArrayList<>();
        productBook.getImages().forEach(image -> {
            String nameImage = image.getImage().getName();
            listImage.add(nameImage);
        });
        return ProductBookResponse.builder()
                .id(productBook.getId())
                .name(productBook.getName())
                .slug(productBook.getSlug())
                .description(productBook.getDescription())
                .price(productBook.getPrice())
                .quantity(productBook.getQuantity())
                .sku(productBook.getSku())
                .enable(productBook.getEnable())
                .featured(productBook.getFeatured())
                .viewCount(productBook.getViewCount())
                .digital(productBook.getDigital())
                .createDate(productBook.getCreateDate())
                .type(productBook.getType() != null ? productBook.getType().name() : null)
                .categoryId(productBook.getCategory() != null ? productBook.getCategory().getId() : null)
                .brandId(productBook.getBrand() != null ? productBook.getBrand().getId() : null)
                .thumbnailId(productBook.getThumbnail() != null ? productBook.getThumbnail().getId() : null)
                .userId(productBook.getUser() != null ? productBook.getUser().getId() : null)
                .isbn(productBook.getIsbn())
                .volumeNumber(productBook.getVolumeNumber())
                .bookAuthor(productBook.getBookAuthor())
                .publisher(productBook.getPublisher())
                .pageCount(productBook.getPageCount())
                .language(productBook.getLanguage())
                .pdfFileId(productBook.getPdfFile() != null ? productBook.getPdfFile().getId() : null)
                .listImage(listImage)
                .build();
    }

    private List<ProductImage> validateListImage(List<String> imageNames, ProductBook productBook) {
        List<ProductImage> productImages = new ArrayList<>();

        if (imageNames == null || imageNames.isEmpty()) {
            return productImages;
        }

        int sortOrder = 1;

        for (String imageName : imageNames) {
            Image image = imageRepository.findByName(imageName.trim())
                    .orElseThrow(() -> new RuntimeException("Ảnh phụ không tồn tại: " + imageName));

            ProductImage productImage = ProductImage.builder()
                    .product(productBook)
                    .image(image)
                    .sortOrder(sortOrder++)
                    .caption(null)
                    .build();

            productImages.add(productImage);
        }

        return productImages;
    }


    private void validateRequest(ProductBookRequest request) {
        if (request == null) {
            throw new RuntimeException("Request không được null");
        }

        if (isBlank(request.getName())) {
            throw new RuntimeException("Tên sách không được để trống");
        }

        if (request.getName().trim().length() > 255) {
            throw new RuntimeException("Tên sách không được vượt quá 255 ký tự");
        }

        if (request.getPrice() == null) {
            throw new RuntimeException("Giá sách không được để trống");
        }

        if (request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Giá sách không được âm");
        }

        if (request.getQuantity() < 0) {
            throw new RuntimeException("Số lượng tồn kho không được âm");
        }

        if (isBlank(request.getBookAuthor())) {
            throw new RuntimeException("Tác giả sách không được để trống");
        }

        if (request.getBookAuthor().trim().length() > 255) {
            throw new RuntimeException("Tên tác giả không được vượt quá 255 ký tự");
        }

        if (request.getVolumeNumber() != null && request.getVolumeNumber() < 1) {
            throw new RuntimeException("Số tập của sách phải lớn hơn hoặc bằng 1");
        }

        if (!isBlank(request.getSku())) {
            if (request.getSku().trim().length() > 100) {
                throw new RuntimeException("SKU không được vượt quá 100 ký tự");
            }

            if (productBookRepository.existsBySku(request.getSku().trim())) {
                throw new RuntimeException("SKU đã tồn tại");
            }
        }

        if (!isBlank(request.getIsbn())) {
            if (request.getIsbn().trim().length() > 50) {
                throw new RuntimeException("ISBN không được vượt quá 50 ký tự");
            }

            if (productBookRepository.existsByIsbn(request.getIsbn().trim())) {
                throw new RuntimeException("ISBN đã tồn tại");
            }
        }

        if (request.getPageCount() != null && request.getPageCount() < 1) {
            throw new RuntimeException("Số trang phải lớn hơn hoặc bằng 1");
        }

        if (!isBlank(request.getCategoryName())) {
            categoryRepository.findByName(request.getCategoryName().trim())
                    .orElseThrow(() -> new RuntimeException(
                            "Category không tồn tại: " + request.getCategoryName()
                    ));
        }

        if (!isBlank(request.getBrandName())) {
            brandRepository.findByName(request.getBrandName().trim())
                    .orElseThrow(() -> new RuntimeException(
                            "Brand không tồn tại: " + request.getBrandName()
                    ));
        }

        if (!isBlank(request.getThumbnailName())) {
            imageRepository.findByName(request.getThumbnailName().trim())
                    .orElseThrow(() -> new RuntimeException(
                            "Ảnh thumbnail không tồn tại: " + request.getThumbnailName()
                    ));
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

        if (!isBlank(request.getPdfFileName())) {
            fileResourceRepository.findByName(request.getPdfFileName().trim())
                    .orElseThrow(() -> new RuntimeException(
                            "File PDF không tồn tại: " + request.getPdfFileName()
                    ));
        }
    }

    private String generateUniqueSlug(String name) {
        String baseSlug = toSlug(name);
        String slug = baseSlug;

        int index = 1;

        while (productBookRepository.existsBySlug(slug)) {
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

    private FileResource validateFile(String pdfFileName) {
        if (isBlank(pdfFileName)) {
            return null;
        }

        return fileResourceRepository.findByName(pdfFileName.trim())
                .orElseThrow(() -> new RuntimeException("File PDF không tồn tại: " + pdfFileName));
    }

    private Image validateImage(String thumbnailName) {
        if (isBlank(thumbnailName)) {
            return null;
        }

        return imageRepository.findByName(thumbnailName.trim())
                .orElseThrow(() -> new RuntimeException("Ảnh thumbnail không tồn tại: " + thumbnailName));
    }

    private Brand validateBrand(String brandName) {
        if (isBlank(brandName)) {
            return null;
        }
        return brandRepository.findByName(brandName).orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED));
    }

    //
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String trimToNull(String value) {
        if (isBlank(value)) {
            return null;
        }

        return value.trim();
    }

    private Category validateCategory(String name) {
        if (isBlank(name)) {
            return null;
        }

        return categoryRepository.findByName(name.trim())
                .orElseThrow(() -> new RuntimeException("Category không tồn tại: " + name));
    }

    private void validateDuplicateBook(ProductBookRequest request, User user) {
        if (user == null || user.getId() == null) {
            throw new RuntimeException("Không xác định được user hiện tại");
        }

        boolean duplicated = productBookRepository.existsDuplicateBook(
                request.getName().trim(),
                request.getBookAuthor().trim(),
                request.getVolumeNumber(),
                user.getId()
        );

        if (duplicated) {
            throw new AppException(ErrorCode.BOOK_DUPLICATION);
        }
    }
// Kêt thúc các phương thức phục vụ cho viêc thêm mới 1 sản phâm

    @Override
    public PageResponse<ProductBookResponse> getAllProductPagination(int page, int size) {
        // Validate page, size
        if (page < 1) {
            throw new RuntimeException("Page phải lớn hơn hoặc bằng 1");
        }

        if (size < 1) {
            throw new RuntimeException("Size phải lớn hơn hoặc bằng 1");
        }

        // Spring Data JPA page bắt đầu từ 0, nên page từ request cần trừ 1
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<ProductBook> productBookPage = productBookRepository.findAll(pageable);

        List<ProductBookResponse> productBookResponses = productBookPage
                .getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return PageResponse.<ProductBookResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(productBookPage.getTotalPages())
                .totalElements(productBookPage.getTotalElements())
                .data(productBookResponses)
                .build();
    }
    // import file dữ liệu
    @Override
    @Transactional
    public ProductBookImportResponse importProductBooksFromExcel(MultipartFile file) {
        validateExcelFile(file);

        int totalRows = 0;
        int successCount = 0;
        int failedCount = 0;

        List<ProductBookImportError> errors = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            if (sheet == null) {
                throw new RuntimeException("File Excel không có sheet dữ liệu");
            }

            validateExcelHeader(sheet);

            int lastRowNum = sheet.getLastRowNum();

            for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                if (row == null || isEmptyRow(row)) {
                    continue;
                }

                totalRows++;

                ProductBookRequest request = null;

                try {
                    request = mapRowToProductBookRequest(row);

                    createProductBook(request);

                    successCount++;
                } catch (Exception exception) {
                    failedCount++;

                    errors.add(ProductBookImportError.builder()
                            .rowIndex(rowIndex + 1)
                            .name(request != null ? request.getName() : null)
                            .message(exception.getMessage())
                            .build());
                }
            }

            return ProductBookImportResponse.builder()
                    .totalRows(totalRows)
                    .successCount(successCount)
                    .failedCount(failedCount)
                    .errors(errors)
                    .build();

        } catch (Exception exception) {
            throw new RuntimeException("Import Excel thất bại: " + exception.getMessage());
        }
    }

    private void validateExcelFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File Excel không được để trống");
        }

        String fileName = file.getOriginalFilename();

        if (isBlank(fileName)) {
            throw new RuntimeException("Tên file Excel không hợp lệ");
        }

        String lowerName = fileName.toLowerCase();

        if (!lowerName.endsWith(".xlsx") && !lowerName.endsWith(".xls")) {
            throw new RuntimeException("Chỉ hỗ trợ file Excel .xlsx hoặc .xls");
        }
    }
    private void validateExcelHeader(Sheet sheet) {
        Row headerRow = sheet.getRow(0);

        if (headerRow == null) {
            throw new RuntimeException("File Excel thiếu dòng header");
        }

        List<String> expectedHeaders = List.of(
                "name",
                "description",
                "price",
                "quantity",
                "sku",
                "enable",
                "featured",
                "categoryName",
                "brandName",
                "thumbnailName",
                "imageName",
                "isbn",
                "volumeNumber",
                "bookAuthor",
                "publisher",
                "pageCount",
                "language",
                "pdfFileName"
        );

        for (int i = 0; i < expectedHeaders.size(); i++) {
            String actualHeader = getStringCellValue(headerRow.getCell(i));

            if (!expectedHeaders.get(i).equalsIgnoreCase(actualHeader)) {
                throw new RuntimeException(
                        "Header không đúng tại cột " + (i + 1)
                                + ". Mong muốn: " + expectedHeaders.get(i)
                                + ", thực tế: " + actualHeader
                );
            }
        }
    }

    private ProductBookRequest mapRowToProductBookRequest(Row row) {
        String name = getStringCellValue(row.getCell(0));
        String description = getStringCellValue(row.getCell(1));
        BigDecimal price = getBigDecimalCellValue(row.getCell(2));
        Integer quantity = getIntegerCellValue(row.getCell(3));
        String sku = getStringCellValue(row.getCell(4));
        Boolean enable = getBooleanCellValue(row.getCell(5));
        Boolean featured = getBooleanCellValue(row.getCell(6));
        String categoryName = getStringCellValue(row.getCell(7));
        String brandName = getStringCellValue(row.getCell(8));
        String thumbnailName = getStringCellValue(row.getCell(9));
        List<String> imageNames = getListStringCellValue(row.getCell(10));
        String isbn = getStringCellValue(row.getCell(11));
        Integer volumeNumber = getIntegerCellValue(row.getCell(12));
        String bookAuthor = getStringCellValue(row.getCell(13));
        String publisher = getStringCellValue(row.getCell(14));
        Integer pageCount = getIntegerCellValue(row.getCell(15));
        String language = getStringCellValue(row.getCell(16));
        String pdfFileName = getStringCellValue(row.getCell(17));

        return ProductBookRequest.builder()
                .name(name)
                .description(description)
                .price(price)
                .quantity(quantity != null ? quantity : 0)
                .sku(sku)
                .enable(enable)
                .featured(featured)
                .categoryName(categoryName)
                .brandName(brandName)
                .thumbnailName(thumbnailName)
                .imageName(imageNames)
                .isbn(isbn)
                .volumeNumber(volumeNumber)
                .bookAuthor(bookAuthor)
                .publisher(publisher)
                .pageCount(pageCount)
                .language(language)
                .pdfFileName(pdfFileName)
                .build();
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        DataFormatter formatter = new DataFormatter();
        String value = formatter.formatCellValue(cell);

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }

    private BigDecimal getBigDecimalCellValue(Cell cell) {
        String value = getStringCellValue(cell);

        if (isBlank(value)) {
            return null;
        }

        try {
            value = value.replace(",", "").trim();
            return new BigDecimal(value);
        } catch (Exception exception) {
            throw new RuntimeException("Giá trị số không hợp lệ: " + value);
        }
    }

    private Integer getIntegerCellValue(Cell cell) {
        String value = getStringCellValue(cell);

        if (isBlank(value)) {
            return null;
        }

        try {
            value = value.replace(",", "").trim();

            if (value.contains(".")) {
                return new BigDecimal(value).intValue();
            }

            return Integer.parseInt(value);
        } catch (Exception exception) {
            throw new RuntimeException("Giá trị số nguyên không hợp lệ: " + value);
        }
    }

    private Boolean getBooleanCellValue(Cell cell) {
        String value = getStringCellValue(cell);

        if (isBlank(value)) {
            return null;
        }

        value = value.trim().toLowerCase();

        if (value.equals("true") || value.equals("1") || value.equals("yes") || value.equals("y")) {
            return true;
        }

        if (value.equals("false") || value.equals("0") || value.equals("no") || value.equals("n")) {
            return false;
        }

        throw new RuntimeException("Giá trị boolean không hợp lệ: " + value);
    }

    private List<String> getListStringCellValue(Cell cell) {
        String value = getStringCellValue(cell);

        if (isBlank(value)) {
            return new ArrayList<>();
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .toList();
    }

    private boolean isEmptyRow(Row row) {
        if (row == null) {
            return true;
        }

        for (int cellIndex = 0; cellIndex < 18; cellIndex++) {
            String value = getStringCellValue(row.getCell(cellIndex));

            if (!isBlank(value)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public ProductBookResponse enable(String id) {
        return null;
    }

    @Override
    public ProductBookResponse disable(String id) {
        return null;
    }
}
