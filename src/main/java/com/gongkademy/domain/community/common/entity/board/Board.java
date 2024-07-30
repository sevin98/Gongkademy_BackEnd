package com.gongkademy.domain.community.common.entity.board;

import com.gongkademy.domain.community.admin.dto.request.BoardRequestDTO;
import com.gongkademy.domain.community.common.entity.comment.Comment;
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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SuperBuilder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Builder.Default
    private LocalDateTime createTime = LocalDateTime.now();

    @Builder.Default
    private Long likeCount = 0L;    // 좋아요 수

    @Builder.Default
    private Long scrapCount = 0L;   // 스크랩 수

    @Builder.Default
    private Long hit = 0L;

    private Long commentCount;  // 댓글 수 추가

    // Admin_Back
    @Builder.Default
    private Boolean isRead = true; // 댓글을 읽어보았는지? 알림 기능 사용

    @Builder.Default
    private Boolean isReply = false; // 게시글에 답글은 작성 하였는지? 정렬 기준 사용

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

    // Admin_Back
    // 댓글 수 업데이트 메서드
    // 댓글 수 업데이트 메서드
    public void updateCommentCount() {
        this.commentCount = (long) comments.size();
    }

    public void update(BoardRequestDTO boardRequestDTO) {
        this.setTitle(boardRequestDTO.getTitle());
        this.setContent(boardRequestDTO.getContent());}
}
