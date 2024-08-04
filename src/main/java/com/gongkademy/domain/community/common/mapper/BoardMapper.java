package com.gongkademy.domain.community.common.mapper;

import com.gongkademy.domain.community.common.entity.board.QnaBoard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.gongkademy.domain.community.common.entity.board.Board;
import com.gongkademy.domain.community.service.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.service.dto.request.ConsultingBoardRequestDTO;
import com.gongkademy.domain.community.service.dto.response.ConsultingBoardResponseDTO;
import com.gongkademy.domain.community.service.dto.request.QnaBoardRequestDTO;
import com.gongkademy.domain.community.service.dto.response.QnaBoardResponseDTO;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface BoardMapper {

    @Mapping(source = "board.member.id", target = "memberId")
    @Mapping(source = "board.member.nickname", target = "nickname")
    @Mapping(source = "board.comments", target = "comments")
    BoardResponseDTO toBoardDTO(Board board);

    @Mapping(source = "board.member.id", target = "memberId")
    @Mapping(source = "board.member.nickname", target = "nickname")
    @Mapping(source = "board.comments", target = "comments")
    @Mapping(source = "isLiked", target = "isLiked")
    @Mapping(source = "isScrapped", target = "isScrapped")
    BoardResponseDTO toBoardDTOWithLikesAndScraps(Board board, boolean isLiked, boolean isScrapped);

    @Mapping(source = "consultingBoardRequestDTO.memberId", target = "member.id")
    Board toBoardEntity(ConsultingBoardRequestDTO consultingBoardRequestDTO);

    @Mapping(source = "consultingBoard.member.id", target = "memberId")
    @Mapping(source = "consultingBoard.member.nickname", target = "nickname")
    @Mapping(source = "consultingBoard.comments", target = "comments")
    ConsultingBoardResponseDTO toConsultingBoardDTO(Board consultingBoard);

    @Mapping(source = "consultingBoard.member.id", target = "memberId")
    @Mapping(source = "consultingBoard.member.nickname", target = "nickname")
    @Mapping(source = "consultingBoard.comments", target = "comments")
    @Mapping(source = "isLiked", target = "isLiked")
    @Mapping(source = "isScrapped", target = "isScrapped")
    ConsultingBoardResponseDTO toConsultingBoardDTOWithLikesAndScraps(Board consultingBoard, boolean isLiked, boolean isScrapped);

    @Mapping(source = "qnaBoardRequestDto.memberId", target = "member.id")
    QnaBoard toQnaBoardEntity(QnaBoardRequestDTO qnaBoardRequestDto);

    @Mapping(source = "qnaBoard.member.id", target = "memberId")
    @Mapping(source = "qnaBoard.member.nickname", target = "nickname")
    @Mapping(source = "qnaBoard.comments", target = "comments")
    QnaBoardResponseDTO toQnaBoardDTO(QnaBoard qnaBoard);

    @Mapping(source = "qnaBoard.member.id", target = "memberId")
    @Mapping(source = "qnaBoard.member.nickname", target = "nickname")
    @Mapping(source = "qnaBoard.comments", target = "comments")
    @Mapping(source = "isLiked", target = "isLiked")
    @Mapping(source = "isScrapped", target = "isScrapped")
    QnaBoardResponseDTO toQnaBoardDTOWithLikesAndScraps(QnaBoard qnaBoard, boolean isLiked, boolean isScrapped);
}
