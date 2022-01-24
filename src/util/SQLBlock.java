package util;

import java.sql.SQLException;

@FunctionalInterface
public interface SQLBlock {
    void execute() throws SQLException;
}
