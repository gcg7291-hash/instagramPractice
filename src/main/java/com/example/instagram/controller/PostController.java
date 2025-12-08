package com.example.instagram.controller;

import com.example.instagram.dto.request.CommentRequest;
import com.example.instagram.dto.request.PostCreateRequest;
import com.example.instagram.dto.response.CommentResponse;
import com.example.instagram.dto.response.PostResponse;
import com.example.instagram.security.CustomUserDetails;
import com.example.instagram.service.CommentService;
import com.example.instagram.service.LikeService;
import com.example.instagram.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("postCreateRequest", new PostCreateRequest());
        return "post/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute PostCreateRequest postCreateRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        if (bindingResult.hasErrors()) {
            return "post/form";
        }

         postService.create(postCreateRequest, image,  userDetails.getId());

        return "redirect:/";
    }


    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ){
        PostResponse post = postService.getPost(id);

        List<CommentResponse> comments = commentService.getComments(id);

        // ⭐️ 1. currentUserId 변수 선언 및 초기화
        // userDetails가 null일 경우 (비로그인 상태)를 대비하여 null 체크를 합니다.
        Long currentUserId = userDetails != null ? userDetails.getId() : null;

        // ⭐️ 2. isOwner (포스트 삭제 버튼 조건) 변수 선언 및 초기화
        // 현재 사용자가 게시물의 작성자인지 확인합니다.
        boolean isOwner = false;
        if (currentUserId != null && currentUserId.equals(post.getUserId())) {
            isOwner = true;
        }

        // 3. Model에 속성 추가
        model.addAttribute("post", post);
        model.addAttribute("commentRequest", new CommentRequest());
        model.addAttribute("comments", comments);

        // userDetails.getId() 대신 currentUserId를 사용하여 null 체크를 피합니다.
        model.addAttribute("liked", likeService.isLiked(id, currentUserId));

        model.addAttribute("likeCount", likeService.getLikeCount(id));

        // ⭐️ 4. isOwner와 currentUserId를 Model에 추가
        model.addAttribute("isOwner", isOwner);           // 포스트 삭제 버튼 표시 (th:if="${isOwner}")
        model.addAttribute("currentUserId", currentUserId); // 댓글 삭제 버튼 표시 (th:if="${comment.userId == currentUserId}")

        return "post/detail";
    }

    @PostMapping("/{postId}/comments")
    public String createComment(
            @PathVariable Long postId,
            @Valid
            @ModelAttribute CommentRequest commentRequest,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        if(bindingResult.hasErrors()){
            PostResponse post = postService.getPost(postId);
            List<CommentResponse> comments = commentService.getComments(postId);
            model.addAttribute("post", post);
            model.addAttribute("comments", comments);
            model.addAttribute("commentRequest", commentRequest);
            return "post/detail";
        }
        commentService.create(postId, commentRequest, userDetails.getId());
        return "redirect:/posts/" + postId;
    }

    @PostMapping("{id}/like")
    public String toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        likeService.toggleLike(id, userDetails.getId());
        return "redirect:/posts/" + id;
    }

    // ⭐️ [추가] 댓글 삭제 핸들러 (PostController 내에 위치)

    @PostMapping("/{id}/delete") // POST 요청을 사용하여 삭제 처리
    public String deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // 서비스 메서드 호출
        postService.deletePost(id, userDetails.getId());

        // 삭제 후 피드 페이지로 리다이렉트
        return "redirect:/";
    }

    @PostMapping("/{postId}/comments/{commentId}/delete")
    public String deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // CommentService를 직접 호출하여 댓글 삭제 로직을 실행합니다.
        // PostService에 deleteComment를 추가했던 구조는 순환 참조를 일으키므로,
        // 컨트롤러에서 CommentService를 직접 호출하는 것이 가장 적절합니다.
        commentService.deleteComment(commentId, userDetails.getId());

        // 댓글 삭제 후 포스트 상세 페이지로 리다이렉트
        return "redirect:/posts/" + postId;
    }




}
