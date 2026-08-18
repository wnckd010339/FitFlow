package com.acorn.gymmanagement.membership.controller;

import com.acorn.gymmanagement.member.dto.response.MemberDetailResponse;
import com.acorn.gymmanagement.member.service.MemberService;
import com.acorn.gymmanagement.payment.service.PaymentService;
import com.acorn.gymmanagement.membership.dto.request.MembershipProductRequest;
import com.acorn.gymmanagement.membership.service.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/memberships")
public class MembershipController {

    private final MemberService memberService;
    private final PaymentService paymentService;
    private final MembershipService membershipService;

    @GetMapping
    public String index(
            @RequestParam(required = false) Long memberId,
            @RequestParam(defaultValue = "history") String view,
            Model model
    ) {
        MemberDetailResponse selectedMember = memberId == null
                ? null
                : memberService.findDetailResponseById(memberId);

        model.addAttribute("selectedMember", selectedMember);
        String selectedView = "products".equals(view) ? "products" : "history";
        model.addAttribute("selectedView", selectedView);
        model.addAttribute("memberFiltered", selectedMember != null);
        model.addAttribute("members", paymentService.findActiveMembers());
        model.addAttribute("products", membershipService.findAllProducts());
        if (!model.containsAttribute("productForm")) {
            model.addAttribute("productForm", new MembershipProductRequest(
                    "", null, 30, BigDecimal.ZERO, 0, "ACTIVE"));
        }
        return "admin/membership/index";
    }

    @PostMapping("/products")
    public String createProduct(@Valid @ModelAttribute("productForm") MembershipProductRequest request,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("selectedView", "products");
            model.addAttribute("memberFiltered", false);
            model.addAttribute("members", paymentService.findActiveMembers());
            model.addAttribute("products", membershipService.findAllProducts());
            return "admin/membership/index";
        }
        membershipService.createProduct(request);
        redirectAttributes.addFlashAttribute("message", "회원권 상품이 등록되었습니다.");
        return "redirect:/admin/memberships?view=products";
    }

    @PostMapping("/products/{productId}")
    public String updateProduct(@PathVariable Long productId,
                                @Valid @ModelAttribute MembershipProductRequest request,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "상품 입력값을 확인해 주세요.");
            return "redirect:/admin/memberships?view=products";
        }
        membershipService.updateProduct(productId, request);
        redirectAttributes.addFlashAttribute("message", "회원권 상품이 수정되었습니다.");
        return "redirect:/admin/memberships?view=products";
    }
}
