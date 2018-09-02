package com.wxsong.rpc.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.aop.SpringProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.wxsong.common.DeCodeHandler;
import com.wxsong.common.EncodeHandler;
import com.wxsong.common.Request;
import com.wxsong.common.Response;
import com.wxsong.registry.ServiceRegistry;
import com.wxsong.rpc.handler.ServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class RpcServer implements ApplicationContextAware, InitializingBean{

	private ServiceRegistry serviceRegistry; //注册服务的地址
	private String serverAddr; //启动server的地址
	
	private Map<String, Object> handlerBeans = new HashMap<String, Object>();
	
	public RpcServer(ServiceRegistry serviceRegistry, String serverAddr) {
		this.serviceRegistry = serviceRegistry;
		this.serverAddr = serverAddr;
	}

	// 将spring初始化好之后将spring的上下文注入进来
	public void setApplicationContext(ApplicationContext cxt) throws BeansException {
		// 将用于处理的类封装到handlerBeans
		Map<String, Object> beansWithAnnotation = cxt.getBeansWithAnnotation(RpcService.class);
		if(!MapUtils.isEmpty(beansWithAnnotation)) {
			for(Object service:beansWithAnnotation.values()) {
				String interfaceName = service.getClass().getAnnotation(RpcService.class).value().getName();
				handlerBeans.put(interfaceName, service);
			}
		}
	}
	// 在初始话spring操作完成时 进行该函数
	public void afterPropertiesSet() throws Exception {
		// 启动netty server
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.channel(NioServerSocketChannel.class).group(boss, worker)
			.childHandler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel channel) throws Exception {
					channel.pipeline().addLast(new DeCodeHandler(Request.class))// in1
									  .addLast(new EncodeHandler(Response.class)) //out
									  .addLast(new ServerHandler(handlerBeans)); // in2
				}
			}).option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_KEEPALIVE, true);
			String [] arr = serverAddr.split(":");
			String host = arr[0];
			int port = Integer.parseInt(arr[1]);
			ChannelFuture future = bootstrap.bind(host, port).sync();
			//向zk注册
			if(serviceRegistry!=null) {
				serviceRegistry.regist(host,port);
			}
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			worker.shutdownGracefully();
			boss.shutdownGracefully();
		}
		
		
	}

	

}
