package com.wxsong.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

public class ServiceDiscovery {

	private ZkClient client;
	private List<String> servers;

	public ServiceDiscovery() {
		client = new ZkClient(Constant.zkConnected, Constant.TIMEOUT);
		client.subscribeChildChanges(Constant.SERVERROOT, new IZkChildListener() {

			public void handleChildChange(String parent, List<String> list) throws Exception {
				List<String> al = new ArrayList<String>();
				for (String path : list) {
					String data = client.readData(Constant.SERVERROOT+"/"+path);
					al.add(data);
				}
				servers = al;
			}
		});
	}

	public String discover() {
		List<String> al = client.getChildren(Constant.SERVERROOT);
		if (al != null) {
			List<String> tempList = new ArrayList<String>();
			for (String path : al) {
				System.out.println(path);
				String data = client.readData(Constant.SERVERROOT+"/"+path);
				tempList.add(data);
			}
			servers = tempList;
		}
		if (servers.size() == 1) {
			return servers.get(0);
		}
		Random r = new Random();
		return servers.get(r.nextInt(servers.size()));
	}

}
