package com.example.instagram.service;

import com.example.instagram.dto.response.PostResponse;
import com.example.instagram.entity.Post;

import java.util.List;

public interface BookmarkService {
    void toggleBookmark(Long postId, Long userId);
    boolean isBookmarked(Long postId, Long userId);
    List<Post> findBookmarkedPosts(Long userId);
}