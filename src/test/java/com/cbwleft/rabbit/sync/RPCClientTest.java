package com.cbwleft.rabbit.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cbwleft.rabbit.sync.RPCClient;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RPCClientTest {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	RPCClient rpcClient;

	@Test
	public void testSend() {
		String result = rpcClient.send("hello world");
		logger.info(result);
	}

	@Test
	public void testSendAsync() throws InterruptedException, ExecutionException {
		String[] messages = { "hello", "my", "name", "is", "leijun" };
		List<Future<String>> results = new ArrayList<>();
		for (String message : messages) {
			Future<String> result = rpcClient.sendAsync(message);
			results.add(result);
		}
		for (Future<String> future : results) {
			String result = future.get();
			if (result == null) {
				logger.info("message timeout after 5 seconds");
			} else {
				logger.info(result);
			}
		}
	}
}
