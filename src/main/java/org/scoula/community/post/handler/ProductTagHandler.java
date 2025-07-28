package org.scoula.community.post.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.scoula.community.post.domain.CategoryTag;
import org.scoula.community.post.domain.ProductTag;

public class ProductTagHandler extends BaseTypeHandler<ProductTag> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ProductTag tag, JdbcType jdbcType) throws SQLException {
        ps.setString(i, tag.getCode());
    }

    @Override
    public ProductTag getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return ProductTag.fromCode(rs.getString(columnName));
    }

    @Override
    public ProductTag getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return ProductTag.fromCode(rs.getString(columnIndex));
    }

    @Override
    public ProductTag getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return ProductTag.fromCode(cs.getString(columnIndex));
    }
}
