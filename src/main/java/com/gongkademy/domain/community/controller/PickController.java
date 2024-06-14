package com.gongkademy.domain.community.controller;

import com.gongkademy.domain.board.dto.request.PickRequestDTO;
import com.gongkademy.domain.board.dto.response.PickResponseDTO;
import com.gongkademy.domain.board.service.PickService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/picks")
@RequiredArgsConstructor
public class PickController {

    private PickService pickService;

    @PostMapping
    public ResponseEntity<?> createPick(@RequestBody PickRequestDTO pickRequestDTO) {
        PickResponseDTO pickResponseDTO = pickService.createPick(pickRequestDTO);
        return new ResponseEntity<>(pickResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePick(@PathVariable Long id, @RequestBody PickRequestDTO pickRequestDTO) {
        PickResponseDTO pickResponseDTO = pickService.updatePick(id, pickRequestDTO);
        return ResponseEntity.ok(pickResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPick(@PathVariable Long id) {
        PickResponseDTO pickResponseDTO = pickService.getPick(id);
        return ResponseEntity.ok(pickResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<PickResponseDTO>> getAllPicks() {
        List<PickResponseDTO> pickResponseDTOS = pickService.getAllPicks();
        return ResponseEntity.ok(pickResponseDTOS);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePick(@PathVariable Long id) {
        pickService.deletePick(id);
        return ResponseEntity.noContent().build();
    }
}
