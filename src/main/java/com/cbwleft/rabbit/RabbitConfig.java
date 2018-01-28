package com.cbwleft.rabbit;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {

	public static final String QUEUE_RPC = "rpc";

	public static final String QUEUE_ASYNC_RPC = "rpc.async";

	@Bean
	public Queue RPCQueue() {
		return new Queue(QUEUE_RPC);
	}

	@Bean
	public Queue asyncRPCQueue() {
		return new Queue(QUEUE_ASYNC_RPC);
	}

	@Bean
	public AsyncRabbitTemplate asyncRabbitTemplate(RabbitTemplate rabbitTemplate, ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
		return new AsyncRabbitTemplate(rabbitTemplate, container, "reply-address");
	}

}
