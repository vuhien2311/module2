package com.adflex.leadwebhook.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebhookRequest {

    private String timestamp;  // hoặc Instant nếu muốn parse
    private LeadPayload data;
}
