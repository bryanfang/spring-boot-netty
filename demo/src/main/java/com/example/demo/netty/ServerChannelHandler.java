package com.example.demo.netty;

import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Test;
import com.example.demo.netty.mapping.IGetMapping;
import com.example.demo.netty.mapping.IPostMapping;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
@Component
@ChannelHandler.Sharable
public class ServerChannelHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	
	private static final Logger logger = LoggerFactory.getLogger(ServerChannelHandler.class);
	
	@Autowired
	private IGetMapping iGetMapping;
	
	@Autowired
	private IPostMapping iPostMapping;
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		if(logger.isInfoEnabled()) {
			logger.info("Server received {}", request);
		}
		if(request.uri() != "/") {
			ctx.fireChannelRead(request);
		}
		FullHttpResponse response = null;
		if(request.method() == HttpMethod.GET) {
			//will respond to client with the content
			List<Test> list = iGetMapping.dealGetRequest();
			String testStr = JSONObject.toJSONString(list, true);
			ByteBuf content = Unpooled.copiedBuffer(testStr, Charset.defaultCharset());
			response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
			//set response header
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
		}
		else if(request.method() == HttpMethod.POST) {
			iPostMapping.dealPost();
			response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CREATED);
		}
		ctx.channel().writeAndFlush(response);
		ctx.close();
	}
}
