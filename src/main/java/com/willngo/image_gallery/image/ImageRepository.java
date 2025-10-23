package com.willngo.image_gallery.image;

import org.hibernate.annotations.processing.SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ImageRepository {
    
    @Autowired
    private DataSource dataSource; //injects database connection pool

    public void addImage(Image image) {
        String sql = "INSERT INTO images (id, image_file_link, image_key, title, description)" +
                "VALUES (gen_random_uuid(),?, ?, ?, ?)";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, image.getImageFileLink().orElse(null));
            stmt.setString(2, image.getImageKey().orElse(null));
            stmt.setString(3, image.getTitle().orElse(null));
            stmt.setString(4, image.getDescription().orElse(null));

            int rowsAffected = stmt.executeUpdate();

            if(rowsAffected == 0) {
                throw new RuntimeException("Failed to upload image");
            }
        }
        catch(SQLException e) {
            throw new RuntimeException("Error uploading image: " + e.getMessage(), e);
        }
    }

    public Optional<Image> findById(UUID id) {
        String sql = "SELECT id, image_file_link, image_key, title, description FROM images WHERE id = ?";

        try(Connection conn = dataSource.getConnection(); //database connection pool
            PreparedStatement stmt = conn.prepareStatement(sql)) { //automatically closes resources when done to prevent leaks

            //sets first ? parameter with id
            stmt.setObject(1, id);

            ResultSet res = stmt.executeQuery();

            if(res.next()) {
                Image image = new Image();
                image.setId((UUID) res.getObject("id"));
                image.setImageFileLink(res.getString("image_file_link"));
                image.setImageKey(res.getString("image_key"));
                image.setTitle(res.getString("title"));
                image.setDescription(res.getString("description"));

                return Optional.of(image);
            }
            return Optional.empty();
        }
        catch(SQLException e) {
            throw new RuntimeException("Database error when finding image by id: " + e.getMessage(), e);
        }
    }

    public Optional<Image> findByS3Key(String s3Key) {
        String sql = "SELECT id, image_file_link, image_key, title, description " +
                "FROM images WHERE image_key = ?";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, s3Key);
            ResultSet res = stmt.executeQuery();

            if(res.next()) {
                Image image = new Image();
                image.setId((UUID) res.getObject("id"));
                image.setImageFileLink(res.getString("image_file_link"));
                image.setImageKey(res.getString("image_key"));
                image.setTitle(res.getString("title"));
                image.setDescription(res.getString("description"));

                return Optional.of(image);
            }
            return Optional.empty();

        }
        catch(SQLException e) {
            throw new RuntimeException("Error with database: "  + e.getMessage(), e);
        }
    }

    public Optional<Image> findByTitle(String title) {
        String sql = "SELECT id, image_file_link, image_key, title, description FROM images WHERE title = ?";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, title);

            ResultSet res = stmt.executeQuery();

            if(res.next()) {
                Image image = new Image();
                image.setId((UUID) res.getObject("id"));
                image.setImageFileLink(res.getString("image_file_link"));
                image.setImageKey(res.getString("image_key"));
                image.setTitle(res.getString("title"));
                image.setDescription(res.getString("description"));

                return Optional.of(image);
            }
            return Optional.empty();
        }
        catch(SQLException e) {
            throw new RuntimeException("Couldn't find image with title:" + e.getMessage(), e);
        }
    }

    public List<Image> getAllImages(){
        String sql = "SELECT id, image_file_link, image_key, title, description FROM images";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            List<Image> images = new ArrayList<>();
            ResultSet res = stmt.executeQuery();

            while(res.next()) {
                Image image = new Image();
                image.setId((UUID) res.getObject("id"));
                image.setImageFileLink(res.getString("image_file_link"));
                image.setImageKey(res.getString("image_key"));
                image.setTitle(res.getString("title"));
                image.setDescription(res.getString("description"));
                images.add(image);
            }

            return images;

        } catch(SQLException e) {
            throw new RuntimeException("Error with getting database while getting all images:"  + e.getMessage(), e);
        }
    }

    public void delete(UUID id) {
        String sql = "DELETE FROM images WHERE id = ?";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            int rowsAffected = stmt.executeUpdate();

            if(rowsAffected == 0){
                throw new RuntimeException("No image found with ID");
            }
        }
        catch(SQLException e) {
            throw new RuntimeException("Database Error when deleting:" + e.getMessage(), e);
        }
    }

    public void changeTitle(String title, UUID id){
        String sql = "UPDATE images SET title = ? WHERE id = ?";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, title);
            stmt.setObject(2, id);

            int rowsAffected = stmt.executeUpdate();

            if(rowsAffected == 0) {
                throw new RuntimeException("No image found with that id");
            }

        }
        catch(SQLException e) {
            throw new RuntimeException("Unable to find image:" + e.getMessage(), e);
        }
    }

    public void changeDescription(String description, UUID id){
        String sql = "UPDATE images SET description = ? where id = ?";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, description);
            stmt.setObject(2, id);

            int rowsAffected = stmt.executeUpdate();

            if(rowsAffected == 0) {
                throw new RuntimeException("No image found with that id");
            }

        }
        catch(SQLException e) {
            throw new RuntimeException("Database Error when changing description:" + e.getMessage(), e);
        }
    }

}
