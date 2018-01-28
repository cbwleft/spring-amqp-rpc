package com.cbwleft.rabbit.async;

import static com.cbwleft.rabbit.RabbitConfig.QUEUE_ASYNC_RPC;
import static com.cbwleft.rabbit.RabbitConfig.QUEUE_ASYNC_RPC_WITH_FIXED_REPLY;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
public class AsyncRPCServer {
	
	@Autowired
	AmqpTemplate amqpTemplate;
	
	@Autowired
	AsyncTask asyncTask;

	@RabbitListener(queues = QUEUE_ASYNC_RPC)
	public void process(String message, @Header(AmqpHeaders.REPLY_TO) String replyTo) {
		ListenableFuture<String> asyncResult = asyncTask.expensiveOperation(message);
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

	@RabbitListener(queues = QUEUE_ASYNC_RPC_WITH_FIXED_REPLY)
	public void process(String message, @Header(AmqpHeaders.REPLY_TO) String replyTo,
			@Header(AmqpHeaders.CORRELATION_ID) byte[] correlationId) {
		ListenableFuture<String> asyncResult = asyncTask.expensiveOperation(message);
		asyncResult.addCallback(new ListenableFutureCallback<String>() {
			@Override
			public void onSuccess(String result) {
				Message resultMessage = MessageBuilder.withBody(result.getBytes())
						.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
						.setCorrelationId(correlationId)
						.build();
				amqpTemplate.send(replyTo, resultMessage);
			}

			@Override
			public void onFailure(Throwable ex) {

			};
		});
	}

}
