package com.adflex.leadwebhook.service;

import com.adflex.leadwebhook.dto.request.LeadPayload;
import com.adflex.leadwebhook.entity.Lead;
import com.adflex.leadwebhook.entity.LeadStatus;
import com.adflex.leadwebhook.repository.LeadRepository;
import com.adflex.leadwebhook.integration.telegram.TelegramNotifier;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;
    private final TelegramNotifier telegramNotifier;


    @Transactional
    public Lead processIncomingLead(LeadPayload payload) {
        String rawPhone = payload.getSdt();
        String normalizedPhone = normalizePhoneNumber(rawPhone);

        // Tìm lead tồn tại
        return leadRepository.findByPhone(normalizedPhone)
                .map(existing -> handleDuplicateLead(existing))
                .orElseGet(() -> createNewLead(payload, normalizedPhone));
    }

    private Lead handleDuplicateLead(Lead existing) {
        existing.setIsDuplicate(true);
        // updatedAt auto update
        Lead saved = leadRepository.save(existing);
        telegramNotifier.notifyDuplicateLead(saved);
        // Không gửi Zalo ZNS cho lead trùng
        return saved;
    }

    private Lead createNewLead(LeadPayload payload, String normalizedPhone) {
        Lead lead = new Lead();
        lead.setMbRefId(payload.getMbRefId());
        lead.setFullName(payload.getTenNguoiGui());
        lead.setPhone(normalizedPhone);
        lead.setEmail(payload.getEmail());
        lead.setBusinessAddress(payload.getDiaChiDn());
        lead.setIndustryNeeds(payload.getNhuCau());
        lead.setCharterCapital(payload.getCharterCapital());
        lead.setIsDuplicate(false);
        lead.setStatus(LeadStatus.NEW);
        // Org mặc định ULTRA (như mô tả)
        lead.setAssignedToOrg("ULTRA");

        List<String> nameOptions = new ArrayList<>();
        if (payload.getTenDnOption1() != null && !payload.getTenDnOption1().isBlank()) {
            nameOptions.add(payload.getTenDnOption1());
        }
        if (payload.getTenDnOption2() != null && !payload.getTenDnOption2().isBlank()) {
            nameOptions.add(payload.getTenDnOption2());
        }
        if (payload.getTenDnOption3() != null && !payload.getTenDnOption3().isBlank()) {
            nameOptions.add(payload.getTenDnOption3());
        }
        if (payload.getTenDnOption4() != null && !payload.getTenDnOption4().isBlank()) {
            nameOptions.add(payload.getTenDnOption4());
        }
        if (payload.getTenDnOption5() != null && !payload.getTenDnOption5().isBlank()) {
            nameOptions.add(payload.getTenDnOption5());
        }
        lead.setBusinessNameOptions(nameOptions);

        Lead saved = leadRepository.save(lead);

        // Thông báo nội bộ & gửi ZNS async
        telegramNotifier.notifyNewLead(saved);


        return saved;
    }

    /**
     * Chuẩn hóa số điện thoại: bỏ space, -, (), chuyển +84 -> 0
     */
    public String normalizePhoneNumber(String phone) {
        if (phone == null) return null;
        // Remove all non-digit except leading +
        String cleaned = phone.trim();

        // Bỏ mọi ký tự không phải số hoặc +
        cleaned = cleaned.replaceAll("[^\\d+]", "");

        if (cleaned.startsWith("+84")) {
            cleaned = "0" + cleaned.substring(3);
        } else if (cleaned.startsWith("84") && !cleaned.startsWith("840")) {
            cleaned = "0" + cleaned.substring(2);
        }
        // Nếu còn ký tự + ở giữa thì bỏ
        cleaned = cleaned.replace("+", "");
        return cleaned;
    }
}
