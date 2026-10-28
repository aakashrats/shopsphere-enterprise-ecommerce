
package com.shopsphere.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CartItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String sku;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
