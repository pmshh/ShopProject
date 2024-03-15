package com.windsome.controller;

import com.windsome.advice.MemberControllerAdvice;
import com.windsome.controller.product.ProductController;
import com.windsome.dto.board.review.ProductReviewDTO;
import com.windsome.dto.product.InventoryDTO;
import com.windsome.dto.product.ProductColorResponseDTO;
import com.windsome.dto.product.ProductFormDTO;
import com.windsome.dto.product.ProductSizeResponseDTO;
import com.windsome.service.board.ReviewService;
import com.windsome.service.product.InventoryService;
import com.windsome.service.product.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc
@MockBean(JpaMetamodelMappingContext.class)
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
class ProductControllerTest {

    @Autowired MockMvc mockMvc;

    @Autowired private ProductController productController;

    @MockBean private ProductService productService;
    @MockBean private ReviewService reviewService;
    @MockBean private InventoryService inventoryService;
    @MockBean MemberControllerAdvice memberControllerAdvice;

    @Test
    void testShowProductDetail() {
        // Given
        Long productId = 123L;
        Model mockModel = mock(Model.class);
        RedirectAttributes mockRedirectAttributes = mock(RedirectAttributes.class);
        List<InventoryDTO> mockInventoryList = mock(List.class);
        List<ProductColorResponseDTO> mockProductColors = mock(List.class);
        List<ProductSizeResponseDTO> mockProductSizes = mock(List.class);

        ProductFormDTO productFormDTO = new ProductFormDTO();
        Pageable pageable = PageRequest.of(1, 5);
        Page<ProductReviewDTO> reviewPage = mock(Page.class);

        when(inventoryService.getInventories(productId)).thenReturn(mockInventoryList);
        when(productService.getProductSizesByProductId(productId)).thenReturn(mockProductSizes);
        when(productService.getProductColorsByProductId(productId)).thenReturn(mockProductColors);
        when(productService.getProductFormDto(productId)).thenReturn(productFormDTO);
        when(reviewService.getProductReviewList(productId, pageable)).thenReturn(reviewPage);

        // When
        String viewName = productController.showProductDetail(Optional.of(1), productId, mockRedirectAttributes, mockModel);

        // Then
        assertEquals("main/product/product-detail", viewName);
        verify(mockModel).addAttribute(eq("inventories"), eq(mockInventoryList));
        verify(mockModel).addAttribute(eq("productSizes"), eq(mockProductSizes));
        verify(mockModel).addAttribute(eq("productColors"), eq(mockProductColors));
        verify(mockModel).addAttribute(eq("product"), eq(productFormDTO));
        verify(mockModel).addAttribute(eq("reviews"), eq(reviewPage));
        verify(mockModel).addAttribute(eq("maxPage"), eq(5));
    }
}