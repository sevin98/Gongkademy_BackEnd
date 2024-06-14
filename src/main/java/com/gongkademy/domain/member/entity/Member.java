package com.gongkademy.domain.member.entity;

import com.gongkademy.domain.community.entity.board.Board;
import com.gongkademy.domain.community.entity.comment.Comment;
import com.gongkademy.domain.community.entity.comment.CommentLike;
import com.gongkademy.domain.community.entity.pick.Pick;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "memberRoleList") //ElementCollection으로 잡힌 애들은 toString 제외를 해줘야 Lazy 로딩이 안됨
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String email;
    @NotEmpty
    private String nickname;
    @NotEmpty
    private String password;

    private LocalDate birthday;

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private List<MemberRole> memberRoleList = new ArrayList<>();

    public void addRole(MemberRole memberRole) {
        memberRoleList.add(memberRole);
    }

    public void clearRole() {
        memberRoleList.clear();
    }

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Pick> picks = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CommentLike> commentLikes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    @Builder.Default
    private List<Board> boards = new ArrayList<>();


    // 연관관계 편의 메서드
    public void addPick(Pick pick) {
        picks.add(pick);
        pick.setMember(this);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setMember(this);
    }

    public void addCommentLike(CommentLike commentLike) {
        commentLikes.add(commentLike);
        commentLike.setMember(this);
    }

    public void addBoard(Board board) {
        boards.add(board);
        board.setMember(this);
    }

}