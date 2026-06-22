package com.xuannguyen.identity.service.productbook.impl;

import com.xuannguyen.identity.dto.request.productbook.ProductBookCreateRequest;
import com.xuannguyen.identity.dto.request.productbook.ProductBookUpdateRequest;
import com.xuannguyen.identity.dto.response.productbook.ProductBookResponse;
import com.xuannguyen.identity.entity.*;
import com.xuannguyen.identity.repository.*;
import com.xuannguyen.identity.service.productbook.ProductBookService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
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

    @Override
    @Transactional
    public ProductBookResponse create(ProductBookCreateRequest request) {
        String slug = resolveSlug(request.getName(), request.getSlug());
        validateCreateRequest(request, slug);
        Category category = findCategory(request.getCategoryId());
        Brand brand = findBrand(request.getBrandId());
        Image thumbnail = findImage(request.getThumbnailId());
        FileResource pdfFile = findFileResource(request.getPdfFileId());
        User user = findUser(request.getUserId());

        ProductBook book = ProductBook.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .price(request.getPrice())
                .salePrice(request.getSalePrice())
                .quantity(request.getQuantity())
                .sku(request.getSku())
                .enable(request.getEnable() != null ? request.getEnable() : true)
                .featured(request.getFeatured() != null ? request.getFeatured() : false)
                .viewCount(0L)
                .digital(request.getDigital() != null ? request.getDigital() : false)
                .metaTitle(request.getMetaTitle())
                .metaDescription(request.getMetaDescription())
                .metaKeywords(request.getMetaKeywords())
                .type(ProductType.BOOK)
                .category(category)
                .brand(brand)
                .thumbnail(thumbnail)
                .user(user)
                .isbn(request.getIsbn())
                .bookAuthor(request.getBookAuthor())
                .publisher(request.getPublisher())
                .pageCount(request.getPageCount())
                .publicationDate(request.getPublicationDate())
                .language(request.getLanguage())
                .bookFormat(request.getBookFormat())
                .pdfFile(pdfFile)
                .build();

        ProductBook savedBook = productBookRepository.save(book);

        return toResponse(savedBook);
    }

    @Override
    @Transactional
    public ProductBookResponse update(String id, ProductBookUpdateRequest request) {
        ProductBook book = findBookById(id);

        if (request.getName() != null && !request.getName().isBlank()) {
            if (!request.getName().equals(book.getName())
                    && productBookRepository.existsByName(request.getName())) {
                throw new RuntimeException("Tên sách đã tồn tại");
            }

            book.setName(request.getName());
        }

        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            String newSlug = toSlug(request.getSlug());

            if (!newSlug.equals(book.getSlug())
                    && productBookRepository.existsBySlug(newSlug)) {
                throw new RuntimeException("Slug đã tồn tại");
            }

            book.setSlug(newSlug);
        } else if (request.getName() != null && !request.getName().isBlank()) {
            String newSlug = toSlug(request.getName());

            if (!newSlug.equals(book.getSlug())
                    && productBookRepository.existsBySlug(newSlug)) {
                throw new RuntimeException("Slug đã tồn tại");
            }

            book.setSlug(newSlug);
        }

        if (request.getSku() != null && !request.getSku().isBlank()) {
            if (!request.getSku().equals(book.getSku())
                    && productBookRepository.existsBySku(request.getSku())) {
                throw new RuntimeException("SKU đã tồn tại");
            }

            book.setSku(request.getSku());
        }

        if (request.getIsbn() != null && !request.getIsbn().isBlank()) {
            if (!request.getIsbn().equals(book.getIsbn())
                    && productBookRepository.existsByIsbn(request.getIsbn())) {
                throw new RuntimeException("ISBN đã tồn tại");
            }

            book.setIsbn(request.getIsbn());
        }

        if (request.getDescription() != null) {
            book.setDescription(request.getDescription());
        }

        if (request.getPrice() != null) {
            book.setPrice(request.getPrice());
        }

        if (request.getSalePrice() != null) {
            book.setSalePrice(request.getSalePrice());
        }

        if (request.getQuantity() != null) {
            book.setQuantity(request.getQuantity());
        }

        if (request.getEnable() != null) {
            book.setEnable(request.getEnable());
        }

        if (request.getFeatured() != null) {
            book.setFeatured(request.getFeatured());
        }

        if (request.getDigital() != null) {
            book.setDigital(request.getDigital());
        }

        if (request.getMetaTitle() != null) {
            book.setMetaTitle(request.getMetaTitle());
        }

        if (request.getMetaDescription() != null) {
            book.setMetaDescription(request.getMetaDescription());
        }

        if (request.getMetaKeywords() != null) {
            book.setMetaKeywords(request.getMetaKeywords());
        }

        if (request.getCategoryId() != null) {
            book.setCategory(findCategory(request.getCategoryId()));
        }

        if (request.getBrandId() != null) {
            book.setBrand(findBrand(request.getBrandId()));
        }

        if (request.getThumbnailId() != null) {
            book.setThumbnail(findImage(request.getThumbnailId()));
        }

        if (request.getUserId() != null) {
            book.setUser(findUser(request.getUserId()));
        }

        if (request.getBookAuthor() != null) {
            book.setBookAuthor(request.getBookAuthor());
        }

        if (request.getPublisher() != null) {
            book.setPublisher(request.getPublisher());
        }

        if (request.getPageCount() != null) {
            book.setPageCount(request.getPageCount());
        }

        if (request.getPublicationDate() != null) {
            book.setPublicationDate(request.getPublicationDate());
        }

        if (request.getLanguage() != null) {
            book.setLanguage(request.getLanguage());
        }

        if (request.getBookFormat() != null) {
            book.setBookFormat(request.getBookFormat());
        }

        if (request.getPdfFileId() != null) {
            book.setPdfFile(findFileResource(request.getPdfFileId()));
        }

        ProductBook updatedBook = productBookRepository.save(book);

        return toResponse(updatedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductBookResponse getById(String id) {
        return toResponse(findBookById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductBookResponse getBySlug(String slug) {
        ProductBook book = productBookRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với slug: " + slug));

        return toResponse(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductBookResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1),
                Sort.by(Sort.Direction.DESC, "createDate")
        );

        return productBookRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public void delete(String id) {
        ProductBook book = findBookById(id);
        productBookRepository.delete(book);
    }

    @Override
    @Transactional
    public ProductBookResponse enable(String id) {
        ProductBook book = findBookById(id);
        book.setEnable(true);
        return toResponse(productBookRepository.save(book));
    }

    @Override
    @Transactional
    public ProductBookResponse disable(String id) {
        ProductBook book = findBookById(id);
        book.setEnable(false);
        return toResponse(productBookRepository.save(book));
    }

    @Override
    @Transactional
    public ProductBookResponse increaseViewCount(String id) {
        ProductBook book = findBookById(id);

        Long currentViewCount = book.getViewCount() == null ? 0L : book.getViewCount();
        book.setViewCount(currentViewCount + 1);

        return toResponse(productBookRepository.save(book));
    }

    private ProductBook findBookById(String id) {
        return productBookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách với id: " + id));
    }

    private Category findCategory(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) {
            return null;
        }

        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy category với id: " + categoryId));
    }

    private Brand findBrand(String brandId) {
        if (brandId == null || brandId.isBlank()) {
            return null;
        }

        return brandRepository.findById(brandId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy brand với id: " + brandId));
    }

    private Image findImage(String imageId) {
        if (imageId == null || imageId.isBlank()) {
            return null;
        }

        return imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy image với id: " + imageId));
    }

    private FileResource findFileResource(String fileResourceId) {
        if (fileResourceId == null || fileResourceId.isBlank()) {
            return null;
        }

        return fileResourceRepository.findById(fileResourceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy file resource với id: " + fileResourceId));
    }

    private User findUser(Long userId) {
        if (userId == null) {
            return null;
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với id: " + userId));
    }

    private void validateCreateRequest(ProductBookCreateRequest request, String slug) {
        if (productBookRepository.existsByName(request.getName())) {
            throw new RuntimeException("Tên sách đã tồn tại");
        }

        if (productBookRepository.existsBySlug(slug)) {
            throw new RuntimeException("Slug đã tồn tại");
        }

        if (request.getSku() != null
                && !request.getSku().isBlank()
                && productBookRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("SKU đã tồn tại");
        }

        if (request.getIsbn() != null
                && !request.getIsbn().isBlank()
                && productBookRepository.existsByIsbn(request.getIsbn())) {
            throw new RuntimeException("ISBN đã tồn tại");
        }
    }

    private String resolveSlug(String name, String slug) {
        if (slug != null && !slug.isBlank()) {
            return toSlug(slug);
        }

        return toSlug(name);
    }

    private String toSlug(String input) {
        if (input == null || input.isBlank()) {
            throw new RuntimeException("Không thể tạo slug từ giá trị rỗng");
        }

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutAccent = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                .matcher(normalized)
                .replaceAll("");

        return withoutAccent
                .toLowerCase(Locale.ROOT)
                .replaceAll("đ", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    private ProductBookResponse toResponse(ProductBook book) {
        return ProductBookResponse.builder()

                .name(book.getName())
                .slug(book.getSlug())
                .description(book.getDescription())
                .price(book.getPrice())
                .salePrice(book.getSalePrice())
                .quantity(book.getQuantity())
                .sku(book.getSku())
                .enable(book.getEnable())
                .featured(book.getFeatured())
                .viewCount(book.getViewCount())
                .digital(book.getDigital())
                .createDate(book.getCreateDate())
                .metaTitle(book.getMetaTitle())
                .metaDescription(book.getMetaDescription())
                .metaKeywords(book.getMetaKeywords())
                .type(book.getType() != null ? book.getType().name() : null)
                .categoryId(book.getCategory() != null ? book.getCategory().getId() : null)
                .brandId(book.getBrand() != null ? book.getBrand().getId() : null)
                .thumbnailId(book.getThumbnail() != null ? book.getThumbnail().getId() : null)
                .userId(book.getUser() != null ? book.getUser().getId() : null)
                .isbn(book.getIsbn())
                .bookAuthor(book.getBookAuthor())
                .publisher(book.getPublisher())
                .pageCount(book.getPageCount())
                .publicationDate(book.getPublicationDate())
                .language(book.getLanguage())
                .bookFormat(book.getBookFormat())
                .pdfFileId(book.getPdfFile() != null ? book.getPdfFile().getId() : null)
                .build();
    }
}
