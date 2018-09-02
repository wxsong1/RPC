package com.wxsong.client;

import org.objenesis.ObjenesisException;

import com.wxsong.common.DeCodeHandler;
import com.wxsong.common.EncodeHandler;
import com.wxsong.common.Request;
import com.wxsong.common.Response;
import com.wxsong.handler.ClientHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RpcClient {

	private String host;
	private int port;
	private Response response;
	public RpcClient(String host, int port) {
		this.host = host;
		this.port = port;
		response = new Response();
	}


	public Response send(Request request) {
		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup();
		final Object lock = new Object();
		final ClientHandler clientHandler = new ClientHandler(lock);
		try {
			bootstrap.group(group).channel(NioSocketChannel.class)
								  .handler(new ChannelInitializer<Channel>() {

									@Override
									protected void initChannel(Channel channel) throws Exception {
											channel.pipeline().addLast(new EncodeHandler(Request.class))
															  .addLast(new DeCodeHandler(Response.class))
															  .addLast(clientHandler);
									}
								}).option(ChannelOption.SO_KEEPALIVE, true);
			ChannelFuture future = bootstrap.connect(host, port).sync();
			future.channel().writeAndFlush(request);
			synchronized (lock) {
				lock.wait();
			}
			response = clientHandler.getResponse();
			if (response != null) {
				future.channel().closeFuture().sync();
			}
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			group.shutdownGracefully();
		}
		return null;
	}

}
