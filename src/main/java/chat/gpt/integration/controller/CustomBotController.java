package chat.gpt.integration.controller;

import chat.gpt.integration.dto.ChatGptRequest;
import chat.gpt.integration.dto.ChatGptResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

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

    @Value("${openai.api.url}")
    private String apiURL;

    @Value("${openai.api.transcription.url}")
    private String transcriptionUrl;

    @Autowired
    private RestTemplate template;

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/chat")
    public String chat(@RequestParam("prompt") String prompt) {
        ChatGptRequest request = new ChatGptRequest(model, prompt);
        ChatGptResponse chatGptResponse = template.postForObject(apiURL, request, ChatGptResponse.class);
        return chatGptResponse.getChoices().get(0).getMessage().getContent();
    }

    @PostMapping("transcription")
    public ResponseEntity<String> audioTranscript(@RequestParam("audio") MultipartFile audio) throws URISyntaxException {
        String model = "whisper-1";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
        body.set("file",audio.getResource());
        body.set("model",model);
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        RequestEntity<MultiValueMap<String,Object>> request = RequestEntity
                .post(new URI(transcriptionUrl))
                .headers(httpHeaders)
                .body(body);
        ResponseEntity<String> exchange = template.exchange(request, String.class);
        return exchange;
    }

//    @PostMapping("transcription")
//    public ResponseEntity<String> audioTranscript(@RequestParam("audio") MultipartFile audio) throws URISyntaxException, IOException {
//        String model = "whisper-1";
//
//        // Create a MultiValueMap to represent the form data
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("file", audio.getResource());
//        body.add("model", model);
//
//        // Set the Content-Type header to indicate form data
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        // Build the request entity with form data
//        RequestEntity<MultiValueMap<String, Object>> requestEntity = RequestEntity
//                .post(new URI(transcriptionUrl))
//                .headers(headers)
//                .body(body);
//
//        try {
//            // Perform the request
//            ResponseEntity<String> exchange =template.exchange(requestEntity, String.class);
//            return exchange;
//        } catch (HttpClientErrorException.BadRequest ex) {
//            // Handle specific error cases if needed
//            System.out.println("Error: " + ex.getResponseBodyAsString());
//            throw ex;
//        }
//    }

    private static MultiValueMap<String, Object> buildMultipartData(File audioFile, String model) {
        try {
            // Create a MultiValueMap to represent form data
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // Add the audio file
            body.add("file", Files.readAllBytes(audioFile.toPath()));

            // Add the model parameter
            body.add("model", model);

            return body;
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception according to your requirements
            return null;
        }


    }
}
