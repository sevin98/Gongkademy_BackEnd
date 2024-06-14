package com.gongkademy.domain.community.service;

import com.gongkademy.domain.community.entity.board.QnaBoard;
import com.gongkademy.domain.community.repository.QnaBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaBoardService {

    private final QnaBoardRepository qnaBoardRepository;

    public List<QnaBoard> findQnaBoardsAll() {
        return qnaBoardRepository.findAll();
    }

}
