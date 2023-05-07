package com.kh.moida.controller;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kh.moida.dao.PostDAO;
import com.kh.moida.vo.PostVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class PostController {


    /**
     * GET : 게시판에 따른 게시물 조회
     * @param boardName (ex - '자유'
     * @return
     */
    @GetMapping("/lounge/{boardName}")
    public ResponseEntity<List<PostVO>> postList(@PathVariable String boardName) {
        System.out.println("BOARD_NAME : " + boardName);
        PostDAO dao = new PostDAO();
        List<PostVO> list = dao.postVOList(boardName);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    @GetMapping("/lounge/post/{postId}")
    public ResponseEntity<PostVO> viewPost(@PathVariable int postId) {
        System.out.println("Post Id : " + postId);
        PostDAO dao = new PostDAO();
        PostVO post = dao.getPostById(postId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(post, HttpStatus.OK);
    }


}
