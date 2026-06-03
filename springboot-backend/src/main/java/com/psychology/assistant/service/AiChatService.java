package com.psychology.assistant.service;

import com.psychology.assistant.config.AiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatService.class);

    @Resource
    private AiProperties aiProperties;

    private final RestTemplate restTemplate;

    public AiChatService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(40000);
        this.restTemplate = new RestTemplate(factory);
    }

    public String generateReply(List<Map<String, String>> messages) {
        String latestUserMessage = messages.isEmpty() ? "" : messages.get(messages.size() - 1).get("content");

        if (isAiDisabled()) {
            log.warn("AI service disabled: missing api-key/base-url/model, fallback reply used");
            return fallbackReply(latestUserMessage);
        }

        String requestUrl = normalizeChatCompletionsUrl(aiProperties.getBaseUrl());

        try {
            Map<String, Object> requestBody = new LinkedHashMap<String, Object>();
            requestBody.put("model", aiProperties.getModel());
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.72);
            requestBody.put("stream", false);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiProperties.getApiKey().trim());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(requestBody, headers);

            log.info("Calling AI model: provider-compatible-url={}, model={}", requestUrl, aiProperties.getModel());

            ResponseEntity<Map> response = restTemplate.exchange(
                requestUrl,
                HttpMethod.POST,
                entity,
                Map.class
            );

            String content = extractAssistantText(response.getBody(), latestUserMessage);
            log.info("AI response received successfully");
            return content;
        } catch (HttpStatusCodeException exception) {
            log.error("AI HTTP error: status={}, body={}", exception.getStatusCode(), exception.getResponseBodyAsString());
            return fallbackReply(latestUserMessage);
        } catch (RestClientException exception) {
            log.error("AI request failed: {}", exception.getMessage(), exception);
            return fallbackReply(latestUserMessage);
        } catch (Exception exception) {
            log.error("AI unexpected error: {}", exception.getMessage(), exception);
            return fallbackReply(latestUserMessage);
        }
    }

    private boolean isAiDisabled() {
        return aiProperties.getApiKey() == null
            || aiProperties.getApiKey().trim().isEmpty()
            || "your-deepseek-api-key".equalsIgnoreCase(aiProperties.getApiKey().trim())
            || aiProperties.getBaseUrl() == null
            || aiProperties.getBaseUrl().trim().isEmpty()
            || aiProperties.getModel() == null
            || aiProperties.getModel().trim().isEmpty();
    }

    private String normalizeChatCompletionsUrl(String baseUrl) {
        String url = baseUrl.trim();

        if (url.endsWith("/chat/completions")) {
            return url;
        }

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        return url + "/chat/completions";
    }

    private String extractAssistantText(Map body, String latestUserMessage) {
        if (body == null) {
            log.warn("AI response body is null, fallback reply used");
            return fallbackReply(latestUserMessage);
        }

        Object choicesObject = body.get("choices");
        if (!(choicesObject instanceof List)) {
            log.warn("AI response has no valid choices field: {}", body);
            return fallbackReply(latestUserMessage);
        }

        List choices = (List) choicesObject;
        if (choices.isEmpty()) {
            log.warn("AI response choices is empty");
            return fallbackReply(latestUserMessage);
        }

        Object firstChoiceObject = choices.get(0);
        if (!(firstChoiceObject instanceof Map)) {
            log.warn("AI response first choice is invalid");
            return fallbackReply(latestUserMessage);
        }

        Map firstChoice = (Map) firstChoiceObject;
        Object messageObject = firstChoice.get("message");
        if (!(messageObject instanceof Map)) {
            log.warn("AI response message object missing");
            return fallbackReply(latestUserMessage);
        }

        Map message = (Map) messageObject;
        Object contentObject = message.get("content");
        if (contentObject == null) {
            log.warn("AI response content is null");
            return fallbackReply(latestUserMessage);
        }

        String content = String.valueOf(contentObject).trim();
        return content.isEmpty() ? fallbackReply(latestUserMessage) : content;
    }

    private String fallbackReply(String userInput) {
        return "我听到你刚刚提到“"
            + shortText(userInput)
            + "”。我们先不急着解决问题，可以先把这份感受放清楚一点。它更像身体上的疲惫，还是心里一直绷着、放不下来的那种累？";
    }

    private String shortText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "现在的感受";
        }
        String value = text.trim();
        return value.length() > 16 ? value.substring(0, 16) + "..." : value;
    }
}
