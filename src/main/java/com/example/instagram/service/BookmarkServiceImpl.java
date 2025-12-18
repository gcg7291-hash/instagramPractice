package com.example.instagram.service;

import com.example.instagram.entity.Bookmark;
import com.example.instagram.entity.Post;
import com.example.instagram.entity.User;
import com.example.instagram.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PostService postService;
    private final UserService userService;

    @Override
    @Transactional
    public void toggleBookmark(Long postId, Long userId) {
        Optional<Bookmark> existingBookmark = bookmarkRepository.findByPostIdAndUserId(postId, userId);

        if (existingBookmark.isPresent()) {
            bookmarkRepository.delete(existingBookmark.get());
        } else {
            Post post = postService.findById(postId);
            User user = userService.findById(userId);

            Bookmark bookmark = Bookmark.builder()
                    .post(post)
                    .user(user)
                    .build();
            bookmarkRepository.save(bookmark);
        }
    }

    @Override
    public boolean isBookmarked(Long postId, Long userId) {
        return bookmarkRepository.existsByPostIdAndUserId(postId, userId);
    }

    @Override
    public List<Post> findBookmarkedPosts(Long userId) {
        return bookmarkRepository.findByUserId(userId)
                .stream()
                .map(Bookmark::getPost)
                .collect(Collectors.toList());
    }
}