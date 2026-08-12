package com.acorn.gymmanagement.facility.controller;

import com.acorn.gymmanagement.facility.dto.request.EquipmentRegistrationRequest;
import com.acorn.gymmanagement.facility.dto.request.EquipmentSearchCondition;
import com.acorn.gymmanagement.facility.dto.request.MaintenanceRegistrationRequest;
import com.acorn.gymmanagement.security.SessionUser;
import com.acorn.gymmanagement.facility.service.FacilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/facilities")
@RequiredArgsConstructor
public class FacilityController {
    private final FacilityService facilityService;

    @GetMapping
    public String index(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String category,
                        @RequestParam(required = false) String status,
                        Model model) {
        EquipmentSearchCondition condition = new EquipmentSearchCondition(keyword, category, status);
        model.addAttribute("condition", condition);
        model.addAttribute("summary", facilityService.getSummary());
        model.addAttribute("equipmentList", facilityService.findEquipment(condition));
        if (!model.containsAttribute("registration")) {
            model.addAttribute("registration", new EquipmentRegistrationRequest("", "", "", null));
        }
        return "admin/facility/index";
    }

    @PostMapping
    public String register(@Valid @ModelAttribute("registration") EquipmentRegistrationRequest request,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("condition", new EquipmentSearchCondition(null, null, null));
            model.addAttribute("summary", facilityService.getSummary());
            model.addAttribute("equipmentList", facilityService.findEquipment(new EquipmentSearchCondition(null, null, null)));
            model.addAttribute("openRegistration", true);
            return "admin/facility/index";
        }
        facilityService.register(request);
        redirectAttributes.addFlashAttribute("message", "기구가 등록되었습니다.");
        return "redirect:/admin/facilities";
    }

    @PostMapping("/{equipmentId}/maintenance")
    public String recordMaintenance(@PathVariable Long equipmentId,
                                    @Valid @ModelAttribute MaintenanceRegistrationRequest request,
                                    @SessionAttribute(SessionUser.SESSION_KEY) SessionUser user,
                                    RedirectAttributes redirectAttributes) {
        facilityService.recordMaintenance(equipmentId, request, user.userId());
        redirectAttributes.addFlashAttribute("message", "시설 점검 이력이 등록되었습니다.");
        return "redirect:/admin/facilities";
    }
}
