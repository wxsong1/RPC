package com.wxsong.registry;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

public class ServiceRegistry {

	private ZkClient client;
	public ServiceRegistry() {
		client = new ZkClient(Constant.zkConnected, Constant.TIMEOUT);
	}
	// Ïòzk×¢²á·þÎñ
	public void regist(String host, int port) {
		boolean exists = client.exists(Constant.SERVERROOT);
		if(!exists) {
			client.create(Constant.SERVERROOT, "", CreateMode.PERSISTENT);
		}
		client.createEphemeralSequential(Constant.SERVERROOT+"/sub_", host+":"+port);
	}

}
