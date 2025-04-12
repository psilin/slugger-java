package src.db;

import java.sql.*;
import src.model.Slug;

public class SlugsDB implements AutoCloseable {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=postgres";

    private Connection connection;

    public SlugsDB() throws Exception {
        this.connection = DriverManager.getConnection(URL);
    }

    public int Save(Slug s) {
        String sql = "INSERT INTO slugs(extid, title, slug, url, locale, products, topics, summary) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // bind the values
            pstmt.setInt(1, s.getId());
            pstmt.setString(2, s.getTitle());
            pstmt.setString(3, s.getSlug());
            pstmt.setString(4, s.getUrl());
            pstmt.setString(5, s.getLocale());
            Array arrayProducts = connection.createArrayOf("TEXT", s.getProducts());
            pstmt.setArray(6, arrayProducts);
            Array arrayTopics = connection.createArrayOf("TEXT", s.getTopics());
            pstmt.setArray(7, arrayTopics);
            pstmt.setString(8, s.getSummary());
            // execute the INSERT statement and get the inserted id
            int insertedRow = pstmt.executeUpdate();
            if (insertedRow > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void close() throws Exception {
        connection.close();
    }
}
