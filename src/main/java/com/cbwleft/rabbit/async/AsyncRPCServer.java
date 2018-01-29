package com.cbwleft.rabbit.async;

import static com.cbwleft.rabbit.RabbitConfig.QUEUE_ASYNC_RPC;
import static com.cbwleft.rabbit.RabbitConfig.QUEUE_ASYNC_RPC_WITH_FIXED_REPLY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
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
	
	Logger logger = LoggerFactory.getLogger(getClass());

	@RabbitListener(queues = QUEUE_ASYNC_RPC)
	public void process(String message, @Header(AmqpHeaders.REPLY_TO) String replyTo) {
		logger.info("recevie message {} and reply to {}", message, replyTo);
		if(replyTo.startsWith("amq.rabbitmq.reply-to")) {
			logger.debug("starting with version 3.4.0, the RabbitMQ server now supports Direct reply-to");
		}else {
			logger.info("fall back to using a temporary reply queue");
		}
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
		logger.info("use a fixed reply queue {}, it is necessary to provide correlation data {} so that replies can be correlated to requests", replyTo, new String(correlationId));
		ListenableFuture<String> asyncResult = asyncTask.expensiveOperation(message);
		asyncResult.addCallback(new ListenableFutureCallback<String>() {
			@Override
			public void onSuccess(String result) {
				/*Message resultMessage = MessageBuilder.withBody(result.getBytes())
						.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
						.setCorrelationId(correlationId)
						.build();
				amqpTemplate.send(replyTo, resultMessage);*/
				amqpTemplate.convertAndSend(replyTo, (Object)result, new MessagePostProcessor() {
					@Override
					public Message postProcessMessage(Message message) throws AmqpException {
						//https://stackoverflow.com/questions/42382307/messageproperties-setcorrelationidstring-is-not-working
						message.getMessageProperties().setCorrelationId(correlationId);
						return message;
					}
				});
			}

			@Override
			public void onFailure(Throwable ex) {

			};
		});
	}

}
