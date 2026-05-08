package Storage;

import java.sql.SQLException;
import java.util.ArrayList;

public abstract class DBCRUD<T> {
    public abstract void insert (T t) throws SQLException;

    public abstract ArrayList<T> readAll() throws SQLException;

    public abstract T readById(int id) throws SQLException;

    public abstract void update(T t) throws SQLException;

    public abstract void delete(int id) throws SQLException;

    protected void handleSQLException(SQLException e) {

    };
}
