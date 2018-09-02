package com.wxsong.rpc.handler;

import java.lang.reflect.Method;
import java.util.Map;

import com.wxsong.common.Request;
import com.wxsong.common.Response;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<Request> {

	private Map<String, Object> handlerBeans;
	
	public ServerHandler(Map<String, Object> handlerBeans) {
		this.handlerBeans = handlerBeans;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Request requset) throws Exception {
		
		Response response = new Response();
		response.setRequestId(requset.getRequestId());
		
		String calssName = requset.getClassName();
		Object obj = handlerBeans.get(calssName);
		if(obj == null) {
			ctx.close();
		}
		String methodName = requset.getMethodName();
		Method method = obj.getClass().getMethod(methodName, requset.getParameterTypes());
		try {
			Object result = method.invoke(obj, requset.getParameters());
			response.setResult(result);
		} catch (Exception e) {
			e.printStackTrace();
			response.setError(e);
		}
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
	}

}
