package com.aggagah.ngepost.service;

import com.aggagah.ngepost.dto.PostRequest;
import com.aggagah.ngepost.dto.PostResponse;

import java.util.List;
import java.util.UUID;

public interface PostService {

    PostResponse createPost(UUID authorId, PostRequest request);

    List<PostResponse> getPostsByAuthor(UUID authorId);

    PostResponse getPostById(UUID postId, UUID authorId);

    PostResponse updatePost(UUID postId, UUID authorId, PostRequest request);

    void deletePost(UUID postId, UUID authorId);
}
