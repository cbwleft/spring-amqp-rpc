package com.cbwleft.rabbit.async;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import static com.cbwleft.rabbit.RabbitConfig.QUEUE_ASYNC_RPC;

@Component
public class AsyncRPCServer {

	@Autowired
	AmqpTemplate amqpTemplate;

	@RabbitListener(queues = QUEUE_ASYNC_RPC)
	public void process(String message, @Header(AmqpHeaders.REPLY_TO) String replyTo) {
		AsyncResult<String> asyncResult = expensiveOperation(message);
		asyncResult.addCallback(new ListenableFutureCallback<String>() {
			@Override
			public void onSuccess(String result) {
				amqpTemplate.convertAndSend(replyTo, result);
			}

			@Override
			public void onFailure(Throwable ex) {

			};
		});
	}

	@Async
	public AsyncResult<String> expensiveOperation(String message) {
		int millis = (int) (Math.random() * 5 * 1000);
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
		String result = message + " executed by " + Thread.currentThread().getName() + " for " + millis + " ms";
		return new AsyncResult<String>(result);
	}

}
