package com.example.instagram.service;


import com.example.instagram.dto.request.PostCreateRequest;
import com.example.instagram.dto.response.PostResponse;
import com.example.instagram.entity.Comment;
import com.example.instagram.entity.Post;
import com.example.instagram.entity.User;
import com.example.instagram.exception.BusinessException;
import com.example.instagram.exception.ErrorCode;
import com.example.instagram.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final UserService userService;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final FileService fileService;
    private final FollowRepository followRepository;



    @Override
    @Transactional
    public PostResponse create(PostCreateRequest postCreateRequest, MultipartFile image, Long userId) {
        User user = userService.findById(userId);

        // 파일을 저장 => 경로
        String imageUrl = null;

        if (image != null && !image.isEmpty()) {
            String fileName = fileService.saveFile(image);
            imageUrl = "/uploads/" + fileName;
        }

        Post post = Post.builder()
                .content(postCreateRequest.getContent())
                .user(user)
                .imageUrl(imageUrl)
                .build();

        Post saved = postRepository.save(post);
        return PostResponse.from(saved);

    }

    @Override
    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }

    @Override
    public PostResponse getPost(Long postId) {
        Post post = findById(postId);
        return PostResponse.from(post);
    }

    @Override
    public List<PostResponse> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(PostResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> getPostsByUsername(String username) {
        User user = userService.findByUsername(username);

        return postRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(PostResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public long countByUserId(Long userId) {
        return postRepository.countByUserId(userId);
    }


    @Override
    public List<PostResponse> getAllPostsWithStats() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(post -> {
                    long likeCount = likeRepository.countByPostId(post.getId());
                    long commentCount = commentRepository.countByPostId(post.getId());
                    return PostResponse.from(post, commentCount, likeCount);
                })
                .collect(Collectors.toList());
    }


    @Override
    public Slice<PostResponse> getFeedPosts(Long userId, Pageable pageable) {
        List<Long> followingIds = followRepository.findFollowingIdsByFollowerId(userId);

        Slice<Post> posts = postRepository.findFeedPostsByUserIds(followingIds, pageable);

        List<PostResponse> content = posts.getContent().stream()
                .map(post -> {
                    long likeCount = likeRepository.countByPostId(post.getId());
                    long commentCount = commentRepository.countByPostId(post.getId());
                    return PostResponse.from(post, commentCount, likeCount);
                })
                .toList();

        return new SliceImpl<>(content, pageable, posts.hasNext());


    }

    @Override
    public Slice<PostResponse> getAllPostsPaging(Pageable pageable) {
        Slice<Post> posts = postRepository.findAllWithUserPaging(pageable);

        List<PostResponse> content = posts.getContent().stream()
                .map(post -> {
                    long likeCount = likeRepository.countByPostId(post.getId());
                    long commentCount = commentRepository.countByPostId(post.getId());

                    return PostResponse.from(post, commentCount, likeCount);
                })
                .toList();

        return new SliceImpl<>(content, pageable, posts.hasNext());
    }


    @Override
    public Slice<PostResponse> searchPosts(String keyword, Pageable pageable) {
        Slice<Post> posts = postRepository.searchByKeyword(keyword, pageable);

        List<PostResponse> content = posts.getContent().stream()
                .map(post -> {
                    long likeCount = likeRepository.countByPostId(post.getId());
                    long commentCount = commentRepository.countByPostId(post.getId());
                    return PostResponse.from(post, commentCount, likeCount);
                })
                .toList();

        return new SliceImpl<>(content, pageable, posts.hasNext());
    }

    @Override
    @Transactional // ⭐️ 트랜잭션 필요
    public void deletePost(Long postId, Long currentUserId) {
        // 1. 게시물 존재 여부 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND)); // POST_NOT_FOUND는 BusinessException을 사용했습니다.

        // 2. 작성자 본인인지 확인 (권한 체크)
        // Post 엔티티에 User 엔티티가 포함되어 있으므로 post.getUser().getId()를 사용합니다.
        if (!post.getUser().getId().equals(currentUserId)) {
            // Spring Security의 AccessDeniedException을 사용하거나, 커스텀 예외를 사용할 수 있습니다.
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        // 3. 파일 삭제 (게시물에 이미지가 있는 경우)
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            // fileService.deleteFile(post.getImageUrl()); // 파일 삭제 서비스가 있다면 호출해야 합니다.
            // 현재는 fileService에 deleteFile 메서드가 없으므로 주석 처리합니다.
        }

        // 4. 게시물 삭제 실행
        // 연관된 댓글, 좋아요 등은 Post 엔티티의 @OneToMany 매핑에 CascadeType.ALL 또는 orphanRemoval = true 설정에 따라 자동으로 삭제됩니다.
        postRepository.delete(post);
    }



}

