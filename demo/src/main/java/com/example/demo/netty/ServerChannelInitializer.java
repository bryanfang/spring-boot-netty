package com.example.demo.netty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

@Component
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

	@Autowired
	private ServerChannelHandler serverChannelHandler;
	
	@Autowired
	private HttpFileHandler httpFileHandler;
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(new HttpRequestDecoder());
		ch.pipeline().addLast(new HttpObjectAggregator(65536));
		ch.pipeline().addLast(new HttpResponseEncoder());
		
		ch.pipeline().addLast(new ChunkedWriteHandler());
		ch.pipeline().addLast(serverChannelHandler);
		ch.pipeline().addLast(httpFileHandler);
	}
	
}
