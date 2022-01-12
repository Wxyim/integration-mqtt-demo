package com.example.integrationmqttdemo.controller;

import com.example.integrationmqttdemo.entity.DemoEntity;
import com.example.integrationmqttdemo.gateway.PublishGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @Autowired
    PublishGateway publishGateway;

    @PostMapping("/send")
    public String send(@RequestBody DemoEntity demoEntity){
        publishGateway.send(demoEntity.getTopic(), 1, demoEntity.getPayload());
        return demoEntity.toString();
    }

}
