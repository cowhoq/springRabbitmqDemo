package cn.edu.bupt.spring.rabbitmq.publisher.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public DirectExchange rideRequestExchange() {
        return new DirectExchange("rideRequestExchange");
    }

    @Bean
    public Queue rideRequestQueue() {
        return new Queue("rideRequestQueue");
    }

    @Bean
    public Binding binding(Queue rideRequestQueue, DirectExchange rideRequestExchange) {
        return BindingBuilder.bind(rideRequestQueue).to(rideRequestExchange).with("rideRequestRouting");
    }
}
