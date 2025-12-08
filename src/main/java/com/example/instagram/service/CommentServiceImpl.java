package com.example.instagram.service;

import com.example.instagram.dto.request.CommentRequest;
import com.example.instagram.dto.response.CommentResponse;
import com.example.instagram.entity.Comment;
import com.example.instagram.entity.Post;
import com.example.instagram.entity.User;
import com.example.instagram.exception.BusinessException;
import com.example.instagram.exception.ErrorCode;
import com.example.instagram.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional // database 동작이라 붙임
public class CommentServiceImpl implements CommentService {

    private final PostService postService;
    private final UserService userService;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public CommentResponse create(Long postId, CommentRequest commentRequest, Long userId) {
        Post post = postService.findById(postId);
        User user = userService.findById(userId);

        Comment comment = Comment.builder()
                .content(commentRequest.getContent())
                .post(post)
                .user(user)
                .build();

        Comment saved = commentRepository.save(comment);
        return CommentResponse.from(saved);
    }

    @Override
    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId).stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional // ⭐️ 데이터 변경을 위해 트랜잭션 필요
    public void deleteComment(Long commentId, Long currentUserId) {

        // 1. 댓글 존재 여부 확인 및 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND)); // 적절한 예외 사용

        // 2. 작성자 본인인지 확인 (권한 체크)
        if (!comment.getUser().getId().equals(currentUserId)) {
            // Spring Security 예외를 던지거나, 적절한 비즈니스 예외를 던집니다.
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        // 3. 댓글 삭제 실행
        commentRepository.delete(comment);
    }


}
