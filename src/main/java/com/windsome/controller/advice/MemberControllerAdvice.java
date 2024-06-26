package com.windsome.controller.advice;

import com.windsome.config.security.CurrentMember;
import com.windsome.entity.member.Member;
import com.windsome.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
@RequiredArgsConstructor
@Slf4j
public class MemberControllerAdvice {

    private final CartService cartService;

    @ModelAttribute("currentMemberInfo")
    public void getCurrentMember(@CurrentMember Member member, Model model) {
        // 현재 로그인한 사용자 정보
        model.addAttribute("currentMember", member);

        if (member != null) {
            // 장바구니에 담긴 상품 개수
            model.addAttribute("totalCartProductCount", cartService.getTotalCartProductCount(member));
        }
    }
}
