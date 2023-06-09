package com.capstoneproject.server.controller;

import com.capstoneproject.server.kafka.message.SimpleMessage;
import com.capstoneproject.server.kafka.service.StudentActivityConsumerService;
import com.capstoneproject.server.kafka.service.StudentActivityProducerService;
import com.capstoneproject.server.payload.response.NoContentDTO;
import com.capstoneproject.server.payload.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dai.le-anh
 * @since 6/9/2023
 */

@RestController
@RequiredArgsConstructor
public class TestController {
    private final StudentActivityProducerService service;

    @GetMapping("sendMessge")
    public Response<NoContentDTO> sendMessage(@RequestParam(value = "message", required = true) String message){
        service.sendMessage(SimpleMessage.builder()
                .message(message)
                .build());
        return Response.<NoContentDTO>newBuilder()
                .setSuccess(true)
                .setMessage("OKe")
                .build();
    }

}
