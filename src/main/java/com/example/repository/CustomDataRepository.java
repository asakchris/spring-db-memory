package com.example.repository;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class CustomDataRepository {
    private static final Logger logger = LoggerFactory.getLogger(CustomDataRepository.class);
    private static final int FETCH_SIZE = 100;
    private static final int BUFFER_SIZE = 8192;
    
    private final EntityManager entityManager;

    public CustomDataRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void streamJsonToOutputStream(Long id, OutputStream outputStream) throws IOException {
        String sql = "SELECT json_data FROM data_table WHERE id = ?";
        Session session = entityManager.unwrap(Session.class);
        
        try {
            session.doWork(connection -> {
                connection.setAutoCommit(false);
                try (PreparedStatement ps = connection.prepareStatement(sql,
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY)) {
                    ps.setFetchDirection(ResultSet.FETCH_FORWARD);
                    ps.setFetchSize(FETCH_SIZE);
                    ps.setLong(1, id);
                    
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            try (InputStream input = rs.getBinaryStream("json_data");
                                 ReadableByteChannel readChannel = Channels.newChannel(input);
                                 WritableByteChannel writeChannel = Channels.newChannel(outputStream)) {
                                
                                ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
                                while (readChannel.read(buffer) != -1) {
                                    buffer.flip();
                                    writeChannel.write(buffer);
                                    buffer.compact();
                                }
                                buffer.flip();
                                while (buffer.hasRemaining()) {
                                    writeChannel.write(buffer);
                                }
                            }
                        }
                    }
                } catch (SQLException | IOException e) {
                    throw new RuntimeException("Error streaming data", e);
                }
            });
        } catch (Exception e) {
            logger.error("Error streaming JSON data for id: {}", id, e);
            throw new IOException("Error streaming JSON data", e);
        }
    }
} 