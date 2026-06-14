package com.xuannguyen.identity.dto.request.producrequest;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private int quantity;
    private String categoryId;
    private String brandId;
    private String thumbnailId;
    private List<String> imageIds;
    private LocalDate createDate = LocalDate.now();
    private Long createUserId;
}
