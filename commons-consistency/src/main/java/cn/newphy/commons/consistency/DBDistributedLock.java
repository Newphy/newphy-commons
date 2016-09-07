package cn.newphy.commons.consistency;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.newphy.commons.consistency.util.UUID;

public class DBDistributedLock implements DistributedLock {
	private static final int OVERDUE = 300;
	private static final String LOCK_MODULE = "CONSISTENCY";

	private JdbcTemplate jdbcTemplate;

	public DBDistributedLock(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public String lock(String locker) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE t_distributed_lock ");
		sql.append("SET STATUS = 1, ");
		sql.append(" lock_time = ?, lock_owner = ?, lock_token = ? ");
		sql.append("WHERE ");
		sql.append("	lock_module = ? ");
		sql.append("AND ( ");
		sql.append("	STATUS = 0 ");
		sql.append("	OR (STATUS = 1 AND lock_time < ?) ");
		sql.append(") ");

		String token = UUID.generate();
		Date now = new Date();
		Date overdue = overdue(now);
		int updates = jdbcTemplate.update(sql.toString(), new Object[] { now, locker, token, LOCK_MODULE, overdue });
		return updates > 0 ? token : null;
	}

	@Override
	public boolean extend(String token) {
		String sql = "UPDATE t_distributed_lock SET status = 1, lock_time = ? WHERE lock_module = ? AND lock_token = ? ";
		int updates = jdbcTemplate.update(sql, new Date(), LOCK_MODULE, token);
		return updates > 0;
	}

	@Override
	public void unlock(String token) {
		String sql = "UPDATE t_distributed_lock SET status = 0, lock_time = ? WHERE lock_module = ? AND lock_token = ? ";
		jdbcTemplate.update(sql, new Date(), LOCK_MODULE, token);
	}

	private Date overdue(Date date) {

		Date overdue = DateUtils.addSeconds(date, -1 * OVERDUE);
		return overdue;
	}
}
