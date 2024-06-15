package com.gongkademy.domain.community.controller;

import com.gongkademy.domain.community.dto.response.BoardResponseDTO;
import com.gongkademy.domain.community.service.BoardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@WebMvcTest(BoardController.class)
@ExtendWith(MockitoExtension.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @Test
    void testGetTop3LatestBoards() throws Exception {
        BoardResponseDTO board1 = BoardResponseDTO.builder().title("Test title1").content("Test Content1").nickname("testuser").build();

        BoardResponseDTO board2 = BoardResponseDTO.builder().
                title("Test title2").
                content("Test Content1").
                nickname("testuser2").build();

        BoardResponseDTO board3 = BoardResponseDTO.builder().
                title("Test title3").nickname("testuser3").content("Test Content3").build();
        BoardResponseDTO board4 = BoardResponseDTO.builder().title("Test title4").content("Test Content4").nickname("testuser4").build();

        List<BoardResponseDTO> boards = Arrays.asList(board1, board2, board3, board4);

        when(boardService.getLatestBoards(3)).thenReturn(boards);

        mockMvc.perform(get("/notice/top3latest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].title", is("Test Title4")))
                .andExpect(jsonPath("$[1].title", is("Test Title3")))
                .andExpect(jsonPath("$[2].title", is("Test Title2")));
    }
}