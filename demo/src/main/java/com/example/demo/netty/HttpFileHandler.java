package com.example.demo.netty;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

@Component
@ChannelHandler.Sharable
public class HttpFileHandler extends ChannelInboundHandlerAdapter {
	
	private final static Logger log = LoggerFactory.getLogger(HttpFileHandler.class);
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		HttpRequest request = null;
		if(msg instanceof HttpRequest) {
			request = (HttpRequest)msg;
		}
		if(request.uri().startsWith("/download") && request.method() == HttpMethod.GET) {
			String url = request.uri();
			//Example: /download?name=<file-name>
			String fileName = url.substring("/download?name=".length());
			String filePath = "C:/Users/vn50cj7/Downloads/" + fileName;
			File file = new File(filePath);
			if(file.isHidden() || !file.exists())
	        {
	            sendError(ctx, HttpResponseStatus.NOT_FOUND);
	            return;
	        }
	        if(!file.isFile())
	        {
	            sendError(ctx, HttpResponseStatus.FORBIDDEN);
	            return;
	        }
	        HttpResponse response = null;
	        try {
                final RandomAccessFile raf = new RandomAccessFile(file, "r");
                long fileLength = raf.length();
                response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileLength);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream");
                response.headers().add(HttpHeaderNames.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()));
                ctx.write(response);
                if(HttpUtil.isKeepAlive(request)) {
                	HttpUtil.setKeepAlive(response, true);
                }
                ChannelFuture sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
                sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                    @Override
                    public void operationComplete(ChannelProgressiveFuture future)
                            throws Exception {
                        log.info("file {} transfer complete.", file.getName());
                        raf.close();
                    }

                    @Override
                    public void operationProgressed(ChannelProgressiveFuture future,
                                                    long progress, long total) throws Exception {
                        if (total < 0) {
                            log.warn("file {} transfer progress: {}", file.getName(), progress);
                        } else {
                            log.debug("file {} transfer progress: {}/{}", file.getName(), progress, total);
                        }
                    }
                });
                ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
                if(!HttpUtil.isKeepAlive(request))
                    lastContentFuture.addListener(ChannelFutureListener.CLOSE);
            } catch (FileNotFoundException e) {
                log.warn("file {} not found", file.getPath());
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, Unpooled.copiedBuffer(String.format("file %s not found", file.getPath()),CharsetUtil.UTF_8));
                
            } catch (IOException e) {
                log.warn("file {} has a IOException: {}", file.getName(), e.getMessage());
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.copiedBuffer(String.format("读取 file %s 发生异常", filePath),CharsetUtil.UTF_8));
            }
	        ctx.writeAndFlush(response);
		}
	}
	
	
	private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, 
                Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        if(ctx.channel().isActive())
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }
}
