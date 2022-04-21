package be.uantwerpen.namingserver;

import org.apache.tomcat.jni.Multicast;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.http.inbound.HttpRequestHandlingMessagingGateway;
import org.springframework.integration.http.inbound.RequestMapping;
import org.springframework.integration.ip.udp.MulticastReceivingChannelAdapter;
import org.springframework.messaging.MessageChannel;

@SpringBootApplication
public class NamingServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NamingServerApplication.class, args);
    }

/*
    @Bean
    public MessageChannel udpInboundChannel()
    {
        return new PublishSubscribeChannel();
    }

    @Bean
    public MulticastReceivingChannelAdapter udpIn() {
        MulticastReceivingChannelAdapter adapter = new MulticastReceivingChannelAdapter("224.0.0.0", 8080);
        adapter.setOutputChannel(udpInboundChannel());
        adapter.setOutputChannelName("udpInbound");
        return adapter;
    }

    @Bean
    public HttpRequestHandlingMessagingGateway inbound() {
        HttpRequestHandlingMessagingGateway gateway =
                new HttpRequestHandlingMessagingGateway(true);
        gateway.setRequestMapping(mapping());
        gateway.setRequestPayloadType(ResolvableType.forClass(String.class));
        gateway.setRequestChannel(udpInboundChannel());
        return gateway;
    }

    @Bean
    public RequestMapping mapping() {
        RequestMapping requestMapping = new RequestMapping();
        requestMapping.setPathPatterns("/discovery");
        requestMapping.setMethods(HttpMethod.POST);
        requestMapping.setParams("name", "ip");
        return requestMapping;
    }
    */
}
