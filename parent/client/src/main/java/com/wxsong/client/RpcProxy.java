package com.wxsong.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import com.wxsong.common.Request;
import com.wxsong.common.Response;
import com.wxsong.registry.ServiceDiscovery;

public class RpcProxy {

	private String serverAddress;
	private ServiceDiscovery serviceDiscovery;
	
	public RpcProxy(String serverAddress){
		this.serverAddress = serverAddress;
	}
	
	
	public RpcProxy(ServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}


	@SuppressWarnings("unchecked")
	public <T> T getProxy(Class<T> clazz) {
		
		T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] {clazz}, new InvocationHandler() {
			
			public Object invoke(Object proxy, Method method, Object[] parameters) throws Throwable {
				Request request = new Request();
				request.setRequestId(UUID.randomUUID().toString());
				request.setMethodName(method.getName());
				request.setClassName(method.getDeclaringClass().getName());
				request.setParameterTypes(method.getParameterTypes());
				request.setParameters(parameters);
				
				//查找服务
				if (serviceDiscovery != null) {
					serverAddress = serviceDiscovery.discover();
				}
				String [] arr = serverAddress.split(":");
				String host = arr[0];
				int port = Integer.parseInt(arr[1]);
				RpcClient client = new RpcClient(host,port);
				Response response = client.send(request);
				if(response!=null && response.getResult()!=null) {
					//返回信息
					if (response.getError() != null) {
						throw response.getError();
					} else {
						return response.getResult();
					}
				}
				return null;
			}
		});
		return proxy;
	}
	
}
