package ru.practicum.shareit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.user.UserClient;

@Configuration
public class WebClientConfig {

    @Value("${shareit-server.url}")
    private String serverUrl;

    @Value("${spring.application.name:shareit-gateway}")
    private String appName;

    @Bean
    public BookingClient bookingClient(RestTemplateBuilder builder) {
        var restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/bookings"))
                .build();

        BookingClient client = new BookingClient(restTemplate);
        client.setAppName(appName);
        return client;
    }

    @Bean
    public ItemClient itemClient(RestTemplateBuilder builder) {
        var restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/items"))
                .build();

        ItemClient client = new ItemClient(restTemplate);
        client.setAppName(appName);
        return client;
    }

    @Bean
    public ItemRequestClient itemRequestClient(RestTemplateBuilder builder) {
        var restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/requests"))
                .build();

        ItemRequestClient client = new ItemRequestClient(restTemplate);
        client.setAppName(appName);
        return client;
    }

    @Bean
    public UserClient userClient(RestTemplateBuilder builder) {
        var restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/users"))
                .build();

        UserClient client = new UserClient(restTemplate);
        client.setAppName(appName);
        return client;
    }
}
