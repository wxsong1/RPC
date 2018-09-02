package com.wxsong.handler;

import com.wxsong.common.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<Response>{

	private Object lock;
	private Response response;
	public ClientHandler(Object lock) {
		this.lock = lock;
	}


	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {

		this.response = response;
		synchronized (lock) {
			lock.notifyAll();
		}
	}
	
	public Response getResponse() {
		return response;
	}

}
