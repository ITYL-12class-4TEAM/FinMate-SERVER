package org.scoula.community.post.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.scoula.community.post.domain.CategoryTag;
import org.scoula.community.post.domain.PostStatus;

public class CategoryTagHandler extends BaseTypeHandler<CategoryTag> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, CategoryTag tag, JdbcType jdbcType) throws SQLException {
        ps.setString(i, tag.getCode());
    }

    @Override
    public CategoryTag getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return CategoryTag.fromCode(rs.getString(columnName));
    }

    @Override
    public CategoryTag getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return CategoryTag.fromCode(rs.getString(columnIndex));
    }

    @Override
    public CategoryTag getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return CategoryTag.fromCode(cs.getString(columnIndex));
    }
}
