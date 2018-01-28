package com.cbwleft.rabbit.sync;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import static com.cbwleft.rabbit.RabbitConfig.QUEUE_SYNC_RPC;

import java.util.concurrent.Future;

@Component
public class RPCClient {

	@Autowired
	AmqpTemplate amqpTemplate;

	public String send(String message) {
		String result = (String) amqpTemplate.convertSendAndReceive(QUEUE_SYNC_RPC, message);
		return result;
	}

	@Async
	public Future<String> sendAsync(String message) {
		return new AsyncResult<String>(send(message));
	}
}
