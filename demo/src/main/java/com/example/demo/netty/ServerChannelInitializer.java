package com.example.demo.netty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

@Component
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

	@Autowired
	private ServerChannelHandler serverChannelHandler;
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(new HttpRequestDecoder());
		ch.pipeline().addLast(new HttpResponseEncoder());
		ch.pipeline().addLast(serverChannelHandler);
	}
	
}
