package cn.newphy.commons.consistency.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import cn.newphy.commons.consistency.ConfirmLevel;
import cn.newphy.commons.consistency.ConfirmStatus;
import cn.newphy.commons.consistency.ConsistencyInfo;
import cn.newphy.commons.consistency.RetryStatus;
import cn.newphy.commons.consistency.util.EnumUtils;

public class ConsistencyDAOImpl implements ConsistencyDAO {
	private JdbcTemplate jdbcTemplate;

	public ConsistencyDAOImpl(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public ConsistencyInfo getConsistencyByTxId(String txId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM t_consistency AS t WHERE t.tx_id = ?");
		return jdbcTemplate.queryForObject(sql.toString(), new Object[] { txId }, consistencyRowMapper());
	}

	@Override
	public ConsistencyInfo getDetail(long id) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT t.*, m.*  FROM t_consistency AS t, t_consistency_message m WHERE t.id = m.consistency_id AND t.id = ?");
		return jdbcTemplate.queryForObject(sql.toString(), new Object[] { id }, consistencyDetailRowMapper());
	}

	@Override
	public void addConsistency(final ConsistencyInfo message) {
		final StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO t_consistency (");
		sql.append("	tx_id,");
		sql.append("	confirm_level,");
		sql.append("	confirm_status, ");
		sql.append("	retry_interval,");
		sql.append("	retry_time,");
		sql.append("	retry_status ");
		sql.append(") ");
		sql.append("VALUES");
		sql.append("	(?, ?, ?, ?, ?, ?)");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				int i = 1;
				PreparedStatement ps = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(i++, message.getTxId());
				ps.setInt(i++, message.getConfirmLevel().getValue());
				ps.setInt(i++, message.getConfirmStatus().getValue());
				ps.setInt(i++, message.getRetryInterval());
				ps.setTimestamp(i++, new Timestamp(message.getRetryTime().getTime()));
				ps.setInt(i++, message.getRetryStatus().ordinal());
				return ps;
			}
		}, keyHolder);

		message.setId(keyHolder.getKey().longValue());
		StringBuilder sql2 = new StringBuilder();
		sql2.append("INSERT INTO t_consistency_message (");
		sql2.append("	consistency_id,");
		sql2.append("	destination,");
		sql2.append("	content");
		sql2.append(") ");
		sql2.append("VALUES");
		sql2.append("	(?, ?, ?)");
		jdbcTemplate.update(sql2.toString(),
				new Object[] { message.getId(), message.getDestination(), message.getContent() });
	}

	@Override
	public List<ConsistencyInfo> queryRetryList(int maxRetry) {
		String sql = "SELECT id, tx_id FROM t_consistency WHERE retry_status = 1 AND retry_time < ? ORDER BY retry_time LIMIT ?";
		List<ConsistencyInfo> list = jdbcTemplate.query(sql, new Object[] { new Date(), maxRetry },
				new RowMapper<ConsistencyInfo>(){
					@Override
					public ConsistencyInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
						ConsistencyInfo message = new ConsistencyInfo();
						message.setId(rs.getLong("id"));
						message.setTxId(rs.getString("tx_id"));
						return message;
					}
		});
		return list;
	}

	@Override
	public void updateConsistency(ConsistencyInfo message) {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE t_consistency ");
		sql.append(
				"SET sync_time = ?, retry_time = ?, confirm_status = ?,  retry_status = ?, sent_time = ?, execute_time = ?, fail_cause = ? ");
		sql.append("WHERE ");
		sql.append("id = ?");
		jdbcTemplate.update(sql.toString(),
				new Object[] { message.getSyncTime(), message.getRetryTime(), message.getConfirmStatus().ordinal(),
						message.getRetryStatus().ordinal(), message.getSentTime(), message.getExecuteTime(),
						message.getFailCause(), message.getId() });
	}

	private RowMapper<ConsistencyInfo> consistencyRowMapper() {
		return new RowMapper<ConsistencyInfo>() {
			@Override
			public ConsistencyInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				ConsistencyInfo consistency = new ConsistencyInfo();
				consistency.setId(rs.getLong("id"));
				consistency.setTxId(rs.getString("tx_id"));
				consistency.setCreateTime(rs.getDate("create_time"));
				consistency.setConfirmLevel(EnumUtils.getEnum(rs.getInt("confirm_level"), ConfirmLevel.class));
				consistency.setConfirmStatus(EnumUtils.getEnum(rs.getInt("confirm_status"), ConfirmStatus.class));
				consistency.setSyncTime(rs.getDate("sync_time"));
				consistency.setRetryInterval(rs.getInt("retry_interval"));
				consistency.setRetryTime(rs.getDate("retry_time"));
				consistency.setRetryStatus(EnumUtils.getEnum(rs.getInt("retry_status"), RetryStatus.class));
				consistency.setSentTime(rs.getTimestamp("sent_time"));
				consistency.setExecuteTime(rs.getTimestamp("execute_time"));
				return consistency;
			}
		};
	}

	private RowMapper<ConsistencyInfo> consistencyDetailRowMapper() {
		return new RowMapper<ConsistencyInfo>() {
			@Override
			public ConsistencyInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				ConsistencyInfo consistency = consistencyRowMapper().mapRow(rs, rowNum);
				consistency.setDestination(rs.getString("destination"));
				consistency.setContent(rs.getString("content"));
				return consistency;
			}
		};
	}
}
