package com.example.instagram.repository;

import com.example.instagram.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    // 특정 사용자의 북마크 목록 조회
    List<Bookmark> findByUserId(Long userId);

    // 특정 게시물과 사용자에 해당하는 북마크 조회 (삭제 및 확인용)
    Optional<Bookmark> findByPostIdAndUserId(Long postId, Long userId);

    // 특정 게시물이 특정 사용자에 의해 북마크되었는지 확인
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}