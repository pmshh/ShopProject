package com.windsome.controller;

import com.windsome.WithAccount;
import com.windsome.constant.ItemSellStatus;
import com.windsome.dto.order.OrderDto;
import com.windsome.dto.order.OrderItemDto;
import com.windsome.entity.Item;
import com.windsome.repository.*;
import com.windsome.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
class OrderControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ItemRepository itemRepository;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;
    @Autowired OrderItemRepository orderItemRepository;
    @Autowired AccountRepository accountRepository;

    @AfterEach
    void afterEach() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        itemRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @WithAccount("test1234")
    @DisplayName("주문 조회 화면 보이는지 테스트")
    @Test
    void orderHist() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attributeExists("maxPage"))
                .andExpect(view().name("order/orderHist"));
    }

    @WithAccount("test1234")
    @DisplayName("주문 취소 테스트")
    @Test
    void cancelOrder() throws Exception {
        Item item = saveItem();

        OrderDto orderDto = getOrderDto(item);
        Long orderId = orderService.order(orderDto, "test1234");

        mockMvc.perform(post("/order/" + orderId + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(String.valueOf(orderId)));
    }

    public Item saveItem() {
        Item item = Item.builder()
                .itemNm("테스트 상품")
                .price(10000)
                .itemDetail("테스트 상품 상세 설명")
                .itemSellStatus(ItemSellStatus.SELL)
                .stockNumber(100)
                .build();
        return itemRepository.save(item);
    }

    private static OrderDto getOrderDto(Item item) {
        List<OrderItemDto> orderItemDtoList = new ArrayList<>();
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setItemId(item.getId());
        orderItemDto.setPrice(item.getPrice());
        orderItemDto.setDiscount(item.getDiscount());
        orderItemDto.setCount(10);
        orderItemDtoList.add(orderItemDto);
        orderItemDto.initPriceAndPoint();

        OrderDto orderDto = new OrderDto("test", "test", "test", "test", "test", "test", orderItemDtoList, 0, 0, 10000, 500, 10000);
        orderDto.initOrderPriceInfo();
        return orderDto;
    }
}