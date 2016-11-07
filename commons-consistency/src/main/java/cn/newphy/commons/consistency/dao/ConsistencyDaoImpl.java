package cn.newphy.commons.consistency.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import cn.newphy.commons.consistency.ConfirmLevel;
import cn.newphy.commons.consistency.ConfirmStatus;
import cn.newphy.commons.consistency.ConsistencyInfo;
import cn.newphy.commons.consistency.RetryStatus;

public class ConsistencyDaoImpl implements ConsistencyDao {
	private Logger logger = LoggerFactory.getLogger(ConsistencyDaoImpl.class);
	
	private JdbcTemplate jdbcTemplate;

	public ConsistencyDaoImpl(DataSource dataSource) {
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
	public void addConsistency(final ConsistencyInfo cinfo) {
		final StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO t_consistency (");
		sql.append("	tx_id,");
		sql.append("	biz_id,");
		sql.append(" destination,");
		sql.append("	confirm_level,");
		sql.append("	confirm_status, ");
		sql.append("	confirm_destination, ");		
		sql.append("	retry_interval,");
		sql.append("	retry_time,");
		sql.append("	retry_status ");	
		sql.append(") ");
		sql.append("VALUES");
		sql.append("	(?, ?, ?, ?, ?, ?, ?, ?, ?)");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				int i = 1;
				PreparedStatement ps = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
				ps.setString(i++, cinfo.getTxId());
				ps.setString(i++, cinfo.getBizId());
				ps.setString(i++, cinfo.getDestination());
				ps.setInt(i++, cinfo.getConfirmLevel().ordinal());
				ps.setInt(i++, cinfo.getConfirmStatus().ordinal());
				ps.setString(i++, cinfo.getConfirmDestination());				
				ps.setInt(i++, cinfo.getRetryInterval());
				ps.setTimestamp(i++, new Timestamp(cinfo.getRetryTime().getTime()));
				ps.setInt(i++, cinfo.getRetryStatus().ordinal());
				return ps;
			}
		}, keyHolder);

		cinfo.setId(keyHolder.getKey().longValue());
		StringBuilder sql2 = new StringBuilder();
		sql2.append("INSERT INTO t_consistency_message (");
		sql2.append("	consistency_id,");
		sql2.append("	destination,");
		sql2.append("	content");
		sql2.append(") ");
		sql2.append("VALUES");
		sql2.append("	(?, ?, ?)");
		jdbcTemplate.update(sql2.toString(),
				new Object[] { cinfo.getId(), cinfo.getDestination(), cinfo.getContent() });
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
	public void updateConsistency(ConsistencyInfo cinfo) {
		logger.info("~~~ updateConsistency(message={})", cinfo);
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE t_consistency ");
		sql.append(
				"SET first_sent_time = ?, retry_time = ?, retry_count = ?, retry_status = ?, confirm_status = ?,  confirm_sent_time = ?, confirm_execute_time = ?, execute_host = ? , fail_cause = ? ");
		sql.append("WHERE ");
		sql.append("id = ?");
		jdbcTemplate.update(sql.toString(),
				new Object[] { cinfo.getFirstSentTime(), cinfo.getRetryTime(), cinfo.getRetryCount(),
						cinfo.getRetryStatus().ordinal(), cinfo.getConfirmStatus().ordinal(),
						cinfo.getConfirmSentTime(), cinfo.getConfirmExecuteTime(), cinfo.getExecuteHost(),
						cinfo.getFailCause(), cinfo.getId() },
				new int[] { Types.TIMESTAMP, Types.TIMESTAMP, Types.INTEGER, Types.TINYINT, Types.TINYINT,
						Types.TIMESTAMP, Types.TIMESTAMP, Types.VARCHAR, Types.VARCHAR, Types.BIGINT });
		logger.info("~~~ updateConsistency() end");
	}

	private RowMapper<ConsistencyInfo> consistencyRowMapper() {
		return new RowMapper<ConsistencyInfo>() {
			@Override
			public ConsistencyInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				ConsistencyInfo consistency = new ConsistencyInfo();
				consistency.setId(rs.getLong("id"));
				consistency.setTxId(rs.getString("tx_id"));
				consistency.setBizId(rs.getString("biz_id"));
				consistency.setDestination(rs.getString("destination"));
				consistency.setFirstSentTime(rs.getTimestamp("first_sent_time"));
				consistency.setRetryInterval(rs.getInt("retry_interval"));
				consistency.setRetryTime(rs.getTimestamp("retry_time"));
				consistency.setRetryCount(rs.getInt("retry_count"));
				consistency.setRetryStatus(RetryStatus.values()[rs.getInt("retry_status")]);
				consistency.setConfirmLevel(ConfirmLevel.values()[rs.getInt("confirm_level")]);
				consistency.setConfirmStatus(ConfirmStatus.values()[rs.getInt("confirm_status")]);
				consistency.setConfirmDestination(rs.getString("confirm_destination"));
				consistency.setConfirmSentTime(rs.getTimestamp("confirm_sent_time"));
				consistency.setConfirmExecuteTime(rs.getTimestamp("confirm_execute_time"));
				consistency.setExecuteHost(rs.getString("execute_host"));
				consistency.setFailCause(rs.getString("fail_cause"));
				consistency.setCreateTime(rs.getTimestamp("create_time"));
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
