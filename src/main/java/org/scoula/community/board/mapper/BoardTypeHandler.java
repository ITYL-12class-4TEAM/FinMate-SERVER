package org.scoula.community.board.mapper;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.scoula.community.board.domain.BoardType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BoardTypeHandler extends BaseTypeHandler<BoardType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BoardType parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getCode());  // BoardType enum을 DB에 저장할 때 코드값으로 설정
    }

    @Override
    public BoardType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String code = rs.getString(columnName);
        return BoardType.fromCode(code);  // DB에서 읽어온 코드값으로 BoardType enum 반환
    }

    @Override
    public BoardType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String code = rs.getString(columnIndex);
        return BoardType.fromCode(code);  // DB에서 읽어온 코드값으로 BoardType enum 반환
    }

    @Override
    public BoardType getNullableResult(java.sql.CallableStatement cs, int columnIndex) throws SQLException {
        String code = cs.getString(columnIndex);
        return BoardType.fromCode(code);  // DB에서 읽어온 코드값으로 BoardType enum 반환
    }
}
