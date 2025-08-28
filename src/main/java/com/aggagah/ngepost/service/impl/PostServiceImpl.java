package com.aggagah.ngepost.service.impl;

import com.aggagah.ngepost.dto.PostRequest;
import com.aggagah.ngepost.dto.PostResponse;
import com.aggagah.ngepost.entity.Post;
import com.aggagah.ngepost.entity.User;
import com.aggagah.ngepost.exception.BadRequestException;
import com.aggagah.ngepost.exception.ForbiddenException;
import com.aggagah.ngepost.exception.NotFoundException;
import com.aggagah.ngepost.repository.PostRepository;
import com.aggagah.ngepost.repository.UserRepository;
import com.aggagah.ngepost.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public PostResponse createPost(UUID authorId, PostRequest request) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new BadRequestException("Author not found"));

        Post post = Post.builder()
                .content(request.getContent())
                .author(author)
                .build();

        Post saved = postRepository.save(post);
        return mapToResponse(saved);
    }

    @Override
    public List<PostResponse> getPostsByAuthor(UUID authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new BadRequestException("Author not found"));

        return postRepository.findByAuthor(author)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PostResponse getPostById(UUID postId, UUID authorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (!post.getAuthor().getId().equals(authorId)) {
            throw new ForbiddenException("Unauthorized access to this post");
        }
        return mapToResponse(post);
    }

    @Override
    public PostResponse updatePost(UUID postId, UUID authorId, PostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (!post.getAuthor().getId().equals(authorId)) {
            throw new ForbiddenException("Unauthorized access to this post");
        }

        post.setContent(request.getContent());
        return mapToResponse(postRepository.save(post));
    }

    @Override
    public void deletePost(UUID postId, UUID authorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (!post.getAuthor().getId().equals(authorId)) {
            throw new ForbiddenException("Unauthorized access to this post");
        }
        postRepository.delete(post);
    }

    private PostResponse mapToResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .authorId(post.getAuthor().getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
