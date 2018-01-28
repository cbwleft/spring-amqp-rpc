package com.cbwleft.rabbit.async;

import static com.cbwleft.rabbit.RabbitConfig.QUEUE_ASYNC_RPC;
import static com.cbwleft.rabbit.RabbitConfig.QUEUE_ASYNC_RPC_WITH_FIXED_REPLY;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Component
public class AsyncRPCClient {

	@Autowired
	AsyncRabbitTemplate asyncRabbitTemplate;

	@Autowired
	AmqpTemplate amqpTemplate;
	
	Logger logger = LoggerFactory.getLogger(getClass());

	@Async
	public Future<String> sendAsync(String message) {
		String result = (String) amqpTemplate.convertSendAndReceive(QUEUE_ASYNC_RPC, message);
		return new AsyncResult<String>(result);
	}

	public Future<String> sendWithFixedReplay(String message) {
		ListenableFuture<String> future = asyncRabbitTemplate.convertSendAndReceive(QUEUE_ASYNC_RPC_WITH_FIXED_REPLY, message);
		return future;
	}

}
