package com.example.instagram.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)  // user라는 데이터를 사용하지 않으면 로드하지 않음 지연 연산
    @JoinColumn(name = "user_id",  nullable = false)
    private User user;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "post",
            cascade = CascadeType.ALL, // ⭐️ 이 속성을 추가합니다.
            orphanRemoval = true) // 이 속성도 보통 함께 사용하여 고아 객체를 정리합니다.
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>(); // ⭐️ Like 엔티티 필드에도 cascade 적용






    @Builder
    public Post(String content, User user, String imageUrl) {
        this.content = content;
        this.user = user;
        this.imageUrl = imageUrl;
    }
}
