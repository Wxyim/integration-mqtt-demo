package com.example.integrationmqttdemo.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import static org.eclipse.paho.client.mqttv3.MqttConnectOptions.MQTT_VERSION_3_1_1;
import static org.springframework.integration.mqtt.support.MqttHeaders.RECEIVED_QOS;
import static org.springframework.integration.mqtt.support.MqttHeaders.RECEIVED_TOPIC;

@Configuration
public class MqttConfig {

    @Bean
    public MqttPahoClientFactory mqttPahoClientFactory(){
        DefaultMqttPahoClientFactory defaultMqttPahoClientFactory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();

        mqttConnectOptions.setServerURIs(new String[]{"tcp://broker.emqx.io:1883"});
        mqttConnectOptions.setConnectionTimeout(10);
        mqttConnectOptions.setKeepAliveInterval(60);
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setAutomaticReconnect(false);
        mqttConnectOptions.setMqttVersion(MQTT_VERSION_3_1_1);

        defaultMqttPahoClientFactory.setConnectionOptions(mqttConnectOptions);

        return defaultMqttPahoClientFactory;
    }

    @Bean
    public MessageChannel inputMessageChannel(){
        return new DirectChannel();
    }

    @Bean
    public MessageProducer messageProducer(){
        MqttPahoMessageDrivenChannelAdapter mqttPahoMessageDrivenChannelAdapter = new MqttPahoMessageDrivenChannelAdapter("subClient001", mqttPahoClientFactory(), "topic37");

        mqttPahoMessageDrivenChannelAdapter.setQos(1);
        mqttPahoMessageDrivenChannelAdapter.setOutputChannel(inputMessageChannel());

        return mqttPahoMessageDrivenChannelAdapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "inputMessageChannel")
    public MessageHandler handleSubscribeMessage(){
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String topic = message.getHeaders().get(RECEIVED_TOPIC).toString();
                String qos = message.getHeaders().get(RECEIVED_QOS).toString();
                String payload = message.getPayload().toString();
                System.out.println("Topic is:  "+topic+"\n"+"Qos is:  "+qos+"\n"+"Payload is:  "+payload);
            }
        };
    }

    @Bean
    public  MessageChannel outputMessageChannel(){
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "outputMessageChannel")
    public MessageHandler handlePublishMessage(){
        MqttPahoMessageHandler mqttPahoMessageHandler = new MqttPahoMessageHandler("pubClient", mqttPahoClientFactory());

        mqttPahoMessageHandler.setAsync(true);

        return mqttPahoMessageHandler;
    }

}
