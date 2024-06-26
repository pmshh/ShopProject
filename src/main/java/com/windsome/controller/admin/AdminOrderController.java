package com.windsome.controller.admin;

import com.windsome.constant.OrderProductStatus;
import com.windsome.constant.OrderStatus;
import com.windsome.dto.order.AdminPageOrderDTO;
import com.windsome.entity.product.ProductOption;
import com.windsome.repository.product.ProductOptionRepository;
import com.windsome.service.admin.AdminService;
import com.windsome.service.order.OrderService;
import com.windsome.service.product.ProductOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminOrderController {

    private final AdminService adminService;
    private final OrderService orderService;
    private final ProductOptionService productOptionService;

    /**
     * 주문 전체 조회
     */
    @GetMapping("/orders")
    public String getOrderList(@RequestParam(name = "userIdentifier", required = false, defaultValue = "") String userIdentifier,
                               @RequestParam(name = "page", required = false, defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        model.addAttribute("orders", adminService.getOrderList(userIdentifier, pageable));
        model.addAttribute("maxPage", 10);
        return "admin/order/order-management";
    }

    /**
     * 주문 상세 조회
     */
    @GetMapping("/orders/{orderId}")
    public String showOrderDetail(@PathVariable("orderId") Long orderId, Model model) {
        model.addAttribute("orderDetail", orderService.getOrderDetail(orderId));
        model.addAttribute("orderId", orderId);
        return "admin/order/order-detail";
    }

    /**
     * 주문 수정 화면
     */
    @GetMapping("/orders/{orderId}/edit")
    public String updateOrderForm(@PathVariable("orderId") Long orderId, Model model) {
        model.addAttribute("orderDetail", orderService.getOrderDetail(orderId));
        model.addAttribute("orderProductStatus", OrderProductStatus.values());
        model.addAttribute("orderStatus", OrderStatus.values());
        model.addAttribute("orderId", orderId);
        return "admin/order/order-form";
    }

    /**
     * 주문 수정
     */
    @PostMapping("/orders/{orderId}")
    public String updateOrder(AdminPageOrderDTO adminPageOrderDTO, @PathVariable("orderId") Long orderId, RedirectAttributes redirectAttr) {
        adminService.updateOrder(orderId, adminPageOrderDTO);
        redirectAttr.addFlashAttribute("message", "주문이 수정되었습니다.");
        return "redirect:/admin/orders/" + orderId;
    }

    /**
     * 주문 취소
     */
    @DeleteMapping("/orders/cancel")
    public ResponseEntity<String> cancelOrder(@RequestBody Long[] orderIds) {
        try {
            adminService.cancelOrders(orderIds);
            return ResponseEntity.ok().body("주문이 취소되었습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body("주문 취소 도중 오류가 발생하였습니다.");
        }
    }

    /**
     * 주문 상품 옵션 조회
     */
    @GetMapping("/orders/product-options/{productId}")
    public ResponseEntity<Object> getProductOptions(@PathVariable("productId") Long productId) {
        return ResponseEntity.ok().body(adminService.getProductOptionsByProductId(productId));
    }
}
