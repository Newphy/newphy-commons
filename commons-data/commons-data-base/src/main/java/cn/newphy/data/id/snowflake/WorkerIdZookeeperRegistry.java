package cn.newphy.data.id.snowflake;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.github.zkclient.IZkStateListener;
import com.github.zkclient.ZkClient;
import com.github.zkclient.exception.ZkBadVersionException;
import com.github.zkclient.exception.ZkNodeExistsException;

public class WorkerIdZookeeperRegistry implements WorkerIdRegistry {
	private static Logger logger = LoggerFactory.getLogger(WorkerIdZookeeperRegistry.class);

	private static final String ROOT = "/SnowFlakeId";
	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static String hostInfo;
	static {
		try {
			Enumeration<NetworkInterface> ifaces =NetworkInterface.getNetworkInterfaces();
			LOOP_IFACE: for(; ifaces.hasMoreElements();) {
				String ifaceInfo = "";
				NetworkInterface iface = ifaces.nextElement();
				for (Enumeration<InetAddress> addrs = iface.getInetAddresses(); addrs.hasMoreElements();) {
					InetAddress addr = addrs.nextElement();
					if("127.0.0.1".equals(addr.getHostAddress())) {
						continue LOOP_IFACE;
					}
					ifaceInfo += addr.getHostName() + "(" + addr.getHostAddress() + "); ";
				}
				if(ifaceInfo.trim().length() > 0) {
					hostInfo += iface.getName() + "[" + ifaceInfo + "]; ";
				}
			}
		} catch (Exception e) {
			logger.warn("get local ip address error", e);
		}
	}

	private ZkClient zkclient;
	private final String zkhosts;
	private int maxSize = 0;
	private ConcurrentHashMap<String, ModuleNode> nodeMap = new ConcurrentHashMap<String, ModuleNode>();

	public WorkerIdZookeeperRegistry(String zkhosts, int connectionTimeout, int maxSize) {
		this.zkhosts = zkhosts;
		this.zkclient = new ZkClient(zkhosts, connectionTimeout);
		this.maxSize = maxSize;
		this.zkclient.subscribeStateChanges(new IZkStateListener() {
			@Override
			public void handleStateChanged(KeeperState state) throws Exception {
				logger.info("~~~ zookeeper change state: {} ~~~", state);
			}

			@Override
			public void handleNewSession() throws Exception {
				synchronized (WorkerIdZookeeperRegistry.this) {
					for (ModuleNode node : nodeMap.values()) {
						initModuleNode(node);
					}
				}
			}
		});
	}

	@Override
	public synchronized WorkerId register(int datacenterId, String moduleName) {
		String path = concretePath(datacenterId, moduleName);
		ModuleNode node = nodeMap.get(path);
		if (node == null) {
			node = new ModuleNode(path, datacenterId, moduleName);
			initModuleNode(node);
			nodeMap.put(path, node);
		}
		return node.getWorkerId();
	}

	private void initModuleNode(ModuleNode node) {
		logger.info("~~~ init workerId module node(dataCenter={}, moduleName={}, path={}) ~~~",
				node.getWorkerId().getDatacenterId(), node.getWorkerId().getModuleName(), node.getPath());
		String path = node.getPath();
		// 操作zk
		if (!zkclient.exists(path)) {
			zkclient.createPersistent(path, true);
		}
		if (zkclient.countChildren(path) >= maxSize) {
			throw new IllegalStateException("more than maxSize[" + maxSize + "] of workerId");
		}

		int seq = 0;
		for (;;) {
			Stat stat = new Stat();
			Integer cur = readInt(path, stat);
			seq = (cur == null || cur >= maxSize-1) ? 0 : cur + 1;
			
			Set<String> children = new HashSet<>(zkclient.getChildren(path));
			if (children.size() >= maxSize) {
				throw new IllegalStateException("more than maxSize[" + maxSize + "] of workerId");
			}
			for (;;) {
				if (!children.contains(String.valueOf(seq))) {
					break;
				}
				if (++seq >= maxSize) {
					seq = 0;
				}
			}
			// 获得当前seq
			try {
				String childPath = path + "/" + seq;
				zkclient.writeData(path, String.valueOf(seq).getBytes(UTF8), stat.getVersion());
				// 创建临时节点
				zkclient.createEphemeral(childPath, hostInfo.getBytes(UTF8));
				break;
			} catch (ZkBadVersionException | ZkNodeExistsException e) {
				continue;
			}
		}
		node.getWorkerId().setWorkerIndex(seq);
		logger.info("~~~ finish init workerId module node(dataCenter={}, moduleName={}, path={}, workerIndex={}) ~~~",
				node.getWorkerId().getDatacenterId(), node.getWorkerId().getModuleName(), node.getPath(), seq);
	}


	private Integer readInt(String path, Stat stat) {
		byte[] data = zkclient.readData(path, stat);
		if (data == null) {
			return null;
		}
		return Integer.parseInt(new String(data, UTF8));
	}


	/**
	 * 获得zk的路径
	 * 
	 * @param datacenterId
	 * @param moduleName
	 * @return
	 */
	private String concretePath(int datacenterId, String moduleName) {
		String path = ROOT + "/" + datacenterId;
		if(StringUtils.hasText(moduleName)) {
			path += "/" + moduleName;
		}
		return path;
	}

	/**
	 * @return the zkhosts
	 */
	public String getZkhosts() {
		return zkhosts;
	}

	private class ModuleNode {
		private final String path;
		private final WorkerId workerId;

		public ModuleNode(String path, int datacenterId, String moduleName) {
			this.path = path;
			this.workerId = new WorkerId(datacenterId, moduleName);
		}

		/**
		 * @return the path
		 */
		public String getPath() {
			return path;
		}

		/**
		 * @return the workerId
		 */
		public WorkerId getWorkerId() {
			return workerId;
		}
	}
}
