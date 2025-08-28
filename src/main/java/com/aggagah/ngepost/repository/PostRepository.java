package com.aggagah.ngepost.repository;

import com.aggagah.ngepost.entity.Post;
import com.aggagah.ngepost.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findByAuthor(User author);
}
