package chat.gpt.integration.controller;

import chat.gpt.integration.dto.ChatGptRequest;
import chat.gpt.integration.dto.ChatGptResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/bot")
public class CustomBotController {

//    @PostMapping("/chat")
//    public Object chat(@RequestParam("prompt") String prompt){
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.set("Authorization","Bearer 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");
//        ChatGptRequest chatGptRequest = new ChatGptRequest("gpt-3.5-turbo-instruct",prompt);
//
//        HttpEntity<ChatGptRequest> chatGptRequestHttpEntity = new HttpEntity<>(chatGptRequest,httpHeaders);
//
//        ResponseEntity<ChatGptResponse> exchange = restTemplate.exchange("https://api.openai.com/v1/completions", HttpMethod.POST, chatGptRequestHttpEntity,
//                ChatGptResponse.class);
//        return  exchange;
//    }

    @Value("${openai.model}")
    private String model;

    @Value ("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/chat")
    public String chat(@RequestParam("prompt") String prompt){
        ChatGptRequest request=new ChatGptRequest(model, prompt);
        ChatGptResponse chatGptResponse = template.postForObject(apiURL, request, ChatGptResponse.class);
        return chatGptResponse.getChoices().get(0).getMessage().getContent();
    }


}
