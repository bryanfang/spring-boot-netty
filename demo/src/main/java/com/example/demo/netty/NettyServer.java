package com.example.demo.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

@Component
public class NettyServer {
	private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
	private EventLoopGroup boss = new NioEventLoopGroup();
	private EventLoopGroup worker = new NioEventLoopGroup();
	private Channel channel;
	
	@Autowired
	private ServerChannelInitializer serverChannelInitializer;
	
	@Value("${netty.port}")
	private Integer port;
	
	public ChannelFuture start() {
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(boss, worker)
		.option(ChannelOption.SO_BACKLOG, 128)
		.channel(NioServerSocketChannel.class)
		.childHandler(serverChannelInitializer);
		
		ChannelFuture channelFuture = serverBootstrap.bind(port);
		ChannelFuture channelFutureAccept = channelFuture.syncUninterruptibly();
		channel = channelFutureAccept.channel();
		if(channelFutureAccept != null && channelFutureAccept.isSuccess()) {
			logger.info("Netty server starts at {}", port);
		}
		else {
			logger.error("Netty server starts failed.");
		}
		return channelFutureAccept;
	}
	
	public void destroy() {
		if(channel != null) {
			channel.close();
		}
		worker.shutdownGracefully();
		boss.shutdownGracefully();
		logger.info("Netty server shutdow gracefully");
	}
}
