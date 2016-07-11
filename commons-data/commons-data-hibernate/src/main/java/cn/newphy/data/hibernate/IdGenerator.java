package cn.newphy.data.hibernate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import cn.newphy.data.exception.DataException;
import cn.newphy.data.hibernate.jdbc.JdbcHelper;

public class IdGenerator {
	private static Logger logger = LoggerFactory.getLogger(IdGenerator.class);

	// IdGenerator Map
	private static ConcurrentHashMap<String, IdGenerator> idGeneratorMap = new ConcurrentHashMap<String, IdGenerator>();

	public static IdGenerator getIdGenerator(String name) {
		if (!idGeneratorMap.containsKey(name)) {
			IdGenerator idGenerator = new IdGenerator(name);
			idGeneratorMap.putIfAbsent(name, idGenerator);
		}
		return idGeneratorMap.get(name);
	}

	// 当前值
	private AtomicLong cur = new AtomicLong(0);
	// 最大值
	private volatile long top = 0L;
	// 获取步长
	private volatile long step = 50L;
	// jdbcTemplate
	private JdbcHelper jdbcHelper;
	// 名称
	private final String name;
	// 获取id线程
	private ExecutorService executor = Executors.newSingleThreadExecutor();

	public IdGenerator(String name) {
		this.name = name;
	}
	
	public void setDataSource(DataSource dataSource) {
		if(dataSource != null) {
			jdbcHelper = new JdbcHelper(dataSource);
		}
	}
	
	public DataSource getDataSource() {
		if(jdbcHelper != null) {
			return jdbcHelper.getDataSource();
		}
		return null;
	}

	public long getId() {
		while (cur.get() >= top) {
			fetch();
		}
		return cur.incrementAndGet();
	}

	private synchronized void fetch() {
		if (cur.get() < top) {
			return;
		}
		checkDB();
		
		Callable<Boolean> call = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				SysIdentity sysId = null;
				while (true) {
					sysId = getSysIdentity();
					if (sysId == null) {
						initSysIdentity();
					} else {
						// 取数据库数据
						IdGenerator.this.step = sysId.step;
						IdGenerator.this.top = sysId.cur + sysId.step;
						IdGenerator.this.cur.set(sysId.cur);

						// 乐观锁，如果更新成功，返回
						sysId.cur = sysId.cur + sysId.step;
						if (!updateSysIdentity(sysId)) {
							continue;
						} else {
							break;
						}
					}
				}
				return Boolean.TRUE;
			}
		};
		
		Future<Boolean> futrue = executor.submit(call);
		try {
			futrue.get(10, TimeUnit.SECONDS);
		}catch (TimeoutException e) {
			logger.error("Request fetch id info[{}] time out", name, e);
			throw new DataException(e);
		}
		catch (Exception e) {
			throw new DataException(e);
		} 

	}

	private void initSysIdentity() {
		SysIdentity sysId = new SysIdentity();
		sysId.code = name;
		sysId.cur = 0L;
		sysId.step = this.step;
		sysId.freq = 0L;
		saveSysIdentity(sysId);
	}

	private SysIdentity getSysIdentity() {
		String sql = "SELECT * FROM sys_identity WHERE code = ?";
		return jdbcHelper.queryForObject(sql, new RowMapper<SysIdentity>() {
			@Override
			public SysIdentity mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				SysIdentity sysId = new SysIdentity();
				sysId.code = rs.getString("code");
				sysId.cur = rs.getLong("cur_val");
				sysId.step = rs.getLong("step_val");
				sysId.freq = rs.getLong("fetch_freq");
				return sysId;
			}
		}, name);
	}

	private boolean updateSysIdentity(SysIdentity sysId) {
		String sql = "UPDATE sys_identity SET cur_val = ?, fetch_freq = ? WHERE code = ? AND fetch_freq = ?";
		return jdbcHelper.update(sql, sysId.cur, (sysId.freq + 1),
				sysId.code, sysId.freq) > 0;
	}

	private void saveSysIdentity(SysIdentity sysId) {
		try {
			String sql = "INSERT INTO sys_identity (code, cur_val, step_val, fetch_freq) values(?, ?, ?, ?)";
			jdbcHelper.update(sql, sysId.code, sysId.cur, sysId.step,
					sysId.freq);
		} catch (Exception e) {
			// 重复插入捕获忽略
		}
	}

	private void checkDB() {
		if (jdbcHelper == null || jdbcHelper.getDataSource() == null) {
			throw new IllegalStateException(
					"dataSource is null, can't fetch id");
		}
	}

	private class SysIdentity {
		private String code;
		private long cur;
		private long step;
		private long freq;

	}

}
