package com.example.demo.netty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelFuture;
@Component
public class StartNettyService implements CommandLineRunner {

	@Autowired
	private NettyServer nettyServer;
	
	@Override
	public void run(String... args) throws Exception {
		ChannelFuture start = nettyServer.start();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				nettyServer.destroy();
			}
		});
		start.channel().closeFuture().syncUninterruptibly();
	}

}
