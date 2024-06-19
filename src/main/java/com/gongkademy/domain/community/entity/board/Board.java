package com.gongkademy.domain.community.entity.board;

import com.gongkademy.domain.community.entity.comment.Comment;
import com.gongkademy.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String title;

    private String content;

    @Builder.Default
    private LocalDateTime createTime = LocalDateTime.now();

    @Builder.Default
    private Long likeCount = 0L;    // 좋아요 수

    @Builder.Default
    private Long scrapCount = 0L;   // 스크랩 수

    private Long hit;

    private Long commentCount;  // 댓글 수 추가

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardType boardType;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    // 연관관계 편의 메서드
    public void setMember(Member member) {
        this.member = member;
        member.getBoards().add(this);
    }

    // 댓글 수 업데이트 메서드
    public void updateCommentCount() {
        this.commentCount = (long) comments.size();
    }
}
