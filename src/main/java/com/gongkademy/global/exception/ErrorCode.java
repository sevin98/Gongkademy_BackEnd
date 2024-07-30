package com.gongkademy.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    //일반
    //TODO: 수정이 필요함
    COMMON_ERROR(HttpStatus.BAD_REQUEST, "처리되지 않은 에러입니다."),

    //Member
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "중복된 닉네임입니다."),
    INVALID_MEMBER_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 회원 아이디입니다."),
    REJOIN_AFTER_ONE_MONTH(HttpStatus.FORBIDDEN, "탈퇴 후 한 달이 지나야 재가입이 가능합니다."),

    NOT_FOUND_MEMBER(HttpStatus.BAD_REQUEST, "회원이 존재하지 않습니다."),

    //JWT
    JWT_MALFORMED(HttpStatus.UNAUTHORIZED, "손상된 토큰입니다."),
    JWT_HEADER_STRING(HttpStatus.UNAUTHORIZED, "토큰 헤더의 문자열이 이상합니다."),
    JWT_NULL_REFRESH(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 없습니다."),
    JWT_EXPIRED_REFRESH(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    JWT_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    JWT_NULL_MEMBER_ID(HttpStatus.UNAUTHORIZED, "memberId가 없습니다"),
    JWT_ERROR(HttpStatus.UNAUTHORIZED, "기타 JWT 에러"),

    NOT_FOUND_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "쿠키에 엑세스 토큰이 없습니다."),

    //Board
    INVALID_BOARD_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 게시글 ID입니다."),

    //QnaBoard
    EMPTY_IMAGE(HttpStatus.NO_CONTENT, "이미지가 존재하지 않습니다."),

    //Comment
    INVALID_COMMENT_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 댓글 ID입니다."),
    INVALID_PARENT_COMMENT_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 부모 댓글 ID입니다."),
    ALREADY_LIKED(HttpStatus.CONFLICT, "이미 좋아요를 눌렀습니다."),
    NOT_YET_LIKED(HttpStatus.CONFLICT, "아직 좋아요를 누르지 않았습니다"),

    //Pick
    INVALID_PICK_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 픽입니다."),

    // 권한 관련
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다"),

    // Course 관련
    DUPLICATE_COURSE_REVIEW(HttpStatus.CONFLICT, "이미 작성한 수강평이 있습니다."),
    NOT_FOUND_COURSE(HttpStatus.BAD_REQUEST, "강좌가 존재하지 않습니다."),
    NOT_FOUND_COURSE_REVIEW(HttpStatus.BAD_REQUEST, "수강평이 존재하지 않습니다."),
    NOT_FOUND_REGIST_COURSE(HttpStatus.BAD_REQUEST, "수강강좌가 존재하지 않습니다."),
    NOT_FOUND_REGIST_LECTURE(HttpStatus.BAD_REQUEST, "수강강의가 존재하지 않습니다"),
    NOT_FOUND_LECTURE(HttpStatus.BAD_REQUEST, "강의가 존재하지 않습니다."),
    NOT_FOUND_COURSE_COMMENT(HttpStatus.BAD_REQUEST, "댓글이 존재하지않습니다."),
    NOT_FOUND_NEXT_LECTURE(HttpStatus.BAD_REQUEST, "다음 강의가 없습니다."),
    NOT_FOUND_PREV_LECTURE(HttpStatus.BAD_REQUEST, "이전 강의가 없습니다."),
    NOT_FOUND_COURSE_NOTICE(HttpStatus.BAD_REQUEST, "공지사항이 존재하지 않습니다."),

    // Lecture 관련
    DUPLICATE_LECTURE_ORDER(HttpStatus.CONFLICT, "이미 존재하는 Lecture_Order입니다."),

    //Notice
    NOT_FOUND_NOTICE(HttpStatus.BAD_REQUEST, "존재하지 않는 공지사항입니다."),

    // Notification 관련
    INVALID_NOTIFICATION_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 알림 ID입니다."),
    WAIT_STATUS_COURSE(HttpStatus.BAD_REQUEST, "대기 상태인 강좌입니다."),
    INVALID_NOTIFICATION_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 알림 타입입니다."),
    
    // S3
    INVALID_FILECATEG(HttpStatus.BAD_REQUEST, "유효하지 않은 카테고리 입니다. (PROFILE, COURSEIMG, COURSENOTE, COURSEINTRO)")
    
    ;
    private final HttpStatus httpStatus;
    private final String message;

}
