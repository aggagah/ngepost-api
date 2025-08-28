package com.aggagah.ngepost.controller;

import com.aggagah.ngepost.dto.BaseResponse;
import com.aggagah.ngepost.dto.PostRequest;
import com.aggagah.ngepost.dto.PostResponse;
import com.aggagah.ngepost.service.PostService;
import com.aggagah.ngepost.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    @Value("${redis.prefix}")
    String REDIS_PREFIX;

    private final PostService postService;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private UUID getAuthorIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String email = jwtUtil.getEmailFromToken(token);
                String redisKey = REDIS_PREFIX + email;
                String storedTokenJson = redisTemplate.opsForValue().get(redisKey);
                if (storedTokenJson != null) {
                    var node = objectMapper.readTree(storedTokenJson);
                    return UUID.fromString(node.get("user").get("id").asText());
                }
            } catch (Exception e) {
                throw new RuntimeException("Invalid token: " + e.getMessage());
            }
        }
        throw new RuntimeException("Authorization header missing or invalid");
    }

    @PostMapping
    public ResponseEntity<BaseResponse> createPost(@RequestBody PostRequest request,
                                                   HttpServletRequest httpRequest) {
        UUID authorId = getAuthorIdFromToken(httpRequest);
        PostResponse postResp = postService.createPost(authorId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("Post created", postResp));
    }


    @GetMapping
    public ResponseEntity<BaseResponse> getMyPosts(HttpServletRequest httpRequest) {
        UUID authorId = getAuthorIdFromToken(httpRequest);
        List<PostResponse> posts = postService.getPostsByAuthor(authorId);
        return ResponseEntity.ok(BaseResponse.success("Posts fetched", posts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getPostById(@PathVariable UUID id,
                                                    HttpServletRequest httpRequest) {
        UUID authorId = getAuthorIdFromToken(httpRequest);
        PostResponse postResp = postService.getPostById(id, authorId);
        return ResponseEntity.ok(BaseResponse.success("Post fetched", postResp));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> updatePost(@PathVariable UUID id,
                                                   @RequestBody PostRequest request,
                                                   HttpServletRequest httpRequest) {
        UUID authorId = getAuthorIdFromToken(httpRequest);
        PostResponse postResp = postService.updatePost(id, authorId, request);
        return ResponseEntity.ok(BaseResponse.success("Post updated", postResp));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deletePost(@PathVariable UUID id,
                                                   HttpServletRequest httpRequest) {
        UUID authorId = getAuthorIdFromToken(httpRequest);
        postService.deletePost(id, authorId);
        return ResponseEntity.ok(BaseResponse.success("Post deleted", null));
    }
}
