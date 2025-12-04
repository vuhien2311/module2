package com.adflex.leadwebhook.integration.telegram;

import com.adflex.leadwebhook.entity.Lead;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class TelegramNotifier {

    @Value("${telegram.bot-token}")
    private String botToken;

    @Value("${telegram.chat-id}")
    private String chatId;

    private final RestTemplate restTemplate = new RestTemplate();

    public void notifyNewLead(Lead lead) {
        String text = String.format(
                "📋 Lead mới:\nTên: %s\nSĐT: %s\nEmail: %s",
                safe(lead.getFullName()),
                safe(lead.getPhone()),
                safe(lead.getEmail())
        );
        sendMessage(text);
    }

    public void notifyDuplicateLead(Lead lead) {
        String text = String.format(
                "🔔 Lead trùng lặp:\nTên: %s\nSĐT: %s",
                safe(lead.getFullName()),
                safe(lead.getPhone())
        );
        sendMessage(text);
    }

    private void sendMessage(String text) {
        try {
            if (botToken == null || botToken.isBlank() ||
                    chatId == null || chatId.isBlank()) {
                log.warn("Telegram botToken/chatId not configured. Skip send.");
                return;
            }

            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            String fullUrl = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("chat_id", chatId)
                    .queryParam("text", text)
                    .build()
                    .toUriString();

            restTemplate.getForObject(fullUrl, String.class);
        } catch (Exception e) {
            log.error("Failed to send Telegram message", e);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
