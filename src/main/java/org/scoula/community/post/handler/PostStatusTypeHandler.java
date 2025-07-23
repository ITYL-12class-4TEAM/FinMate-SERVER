package org.scoula.community.post.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.scoula.community.post.domain.PostStatus;

import java.sql.*;

public class PostStatusTypeHandler extends BaseTypeHandler<PostStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PostStatus status, JdbcType jdbcType) throws SQLException {
        ps.setString(i, status.getCode());
    }

    @Override
    public PostStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return PostStatus.fromCode(rs.getString(columnName));
    }

    @Override
    public PostStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return PostStatus.fromCode(rs.getString(columnIndex));
    }

    @Override
    public PostStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return PostStatus.fromCode(cs.getString(columnIndex));
    }
}
