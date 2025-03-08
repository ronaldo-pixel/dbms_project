
import java.sql.*;

public class table_creation {
    // Change these values based on your Oracle XE setup
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USER = "user_name"; //your username 
    private static final String PASSWORD = "password"; //your password 

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

                stmt.executeUpdate("""
                    CREATE TABLE customers(
                        customer_id NUMBER(10) PRIMARY KEY,
                        last_name VARCHAR2(25) NOT NULL,
                        first_name VARCHAR2(25) NOT NULL,
                        home_phone VARCHAR2(12) NOT NULL,
                        address VARCHAR2(100) NOT NULL,
                        city VARCHAR2(30) NOT NULL,
                        state VARCHAR2(2) NOT NULL,
                        email VARCHAR2(25),
                        cell_phone VARCHAR2(12)
                    )
                """);
    
                // Create Movies table
                stmt.executeUpdate("""
                    CREATE TABLE movies(
                        title_id NUMBER(10) PRIMARY KEY,
                        title VARCHAR2(60) NOT NULL,
                        description VARCHAR2(400) NOT NULL,
                        rating VARCHAR2(4),
                        category VARCHAR2(20),
                        release_date DATE NOT NULL,
                        CONSTRAINT check_rating CHECK(rating IN ('G','PG','PG13','R')),
                        CONSTRAINT check_category CHECK(category IN ('DRAMA', 'COMEDY', 'ACTION', 'CHILD', 'SCIFI', 'DOCUMENTARY'))
                    )
                """);
    
                // Create Media table
                stmt.executeUpdate("""
                    CREATE TABLE media(
                        media_id NUMBER(10) PRIMARY KEY,
                        format VARCHAR2(3) NOT NULL,
                        title_id NUMBER(10) NOT NULL,
                        CONSTRAINT fk_title_id_media FOREIGN KEY(title_id) REFERENCES movies(title_id) ON DELETE cascade
                    )
                """);
    
                // Create Rental History table
                stmt.executeUpdate("""
                    CREATE TABLE rental_history(
                        media_id NUMBER(10),
                        rental_date DATE DEFAULT CURRENT_DATE,
                        customer_id NUMBER(10),
                        return_date DATE,
                        PRIMARY KEY(media_id, rental_date),
                        CONSTRAINT fk_media_id FOREIGN KEY(media_id) REFERENCES media(media_id),
                        CONSTRAINT fk_customer_id FOREIGN KEY(customer_id) REFERENCES customers(customer_id)
                    )
                """);
    
                // Create Actors table
                stmt.executeUpdate("""
                    CREATE TABLE actors(
                        actor_id NUMBER(10) PRIMARY KEY,
                        stage_name VARCHAR2(40) NOT NULL,
                        first_name VARCHAR2(25) NOT NULL,
                        last_name VARCHAR2(25) NOT NULL,
                        birth_date DATE NOT NULL
                    )
                """);
    
                // Create Star Billings table
                stmt.executeUpdate("""
                    CREATE TABLE star_billings(
                        actor_id NUMBER(10),
                        title_id NUMBER(10),
                        comments VARCHAR2(40),
                        PRIMARY KEY(actor_id, title_id),
                        CONSTRAINT fk_actor_id FOREIGN KEY(actor_id) REFERENCES actors(actor_id),
                        CONSTRAINT fk_title_id_star FOREIGN KEY(title_id) REFERENCES movies(title_id)
                    )
                """);
    
                System.out.println("Tables created successfully!");
                conn.close();
        } catch (SQLException e) {
      
            e.printStackTrace();
        }
    }
}
