package com.cbwleft.rabbit.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AsyncRPCClientTest {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	AsyncRPCClient asyncRPCClient;
	
	@Test
	public void testSendAsync() throws InterruptedException, ExecutionException{
		String[] messages = { "hello", "my", "name", "is", "leijun" };
		List<Future<String>> results = new ArrayList<>();
		for (String message : messages) {
			Future<String> result = asyncRPCClient.sendAsync(message);
			results.add(result);
		}
		for (Future<String> future : results) {
			String result = future.get();
			if (result == null) {
				Assert.fail("message will not timeout");
			} else {
				logger.info(result);
			}
		}
	}
	
	@Test
	public void testSendWithFixedReplay() throws InterruptedException, ExecutionException{
		String[] messages = { "hello", "my", "name", "is", "leijun" };
		List<Future<String>> results = new ArrayList<>();
		for (String message : messages) {
			Future<String> result = asyncRPCClient.sendWithFixedReplay(message);
			results.add(result);
		}
		for (Future<String> future : results) {
			String result = future.get();
			if (result == null) {
				Assert.fail("message will not timeout");
			} else {
				logger.info(result);
			}
		}
	}
	

}
