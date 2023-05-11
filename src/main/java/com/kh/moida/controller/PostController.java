package com.kh.moida.controller;

import com.kh.moida.dao.CommentDAO;
import com.kh.moida.dao.PostDAO;
import com.kh.moida.vo.CommentVO;
import com.kh.moida.vo.PostVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class PostController {

    /**
     * GET : 게시판에 따른 게시물 조회
     * @param boardName (ex - 'free' , 'qna')
     * @return ResponseEntity<List<PostVO>>
     */
    @GetMapping("/lounge/{boardName}")
    public ResponseEntity<List<PostVO>> postList(@PathVariable String boardName) {
        System.out.println("BOARD_NAME : " + boardName);
        PostDAO postDAO = new PostDAO();
        List<PostVO> list = postDAO.postVOList(boardName);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    @GetMapping("/lounge/{boardName}/{postId}")
    public ResponseEntity<PostVO> viewPost(@PathVariable String boardName, @PathVariable int postId) {
        System.out.println("Post Id : " + postId);
        PostDAO postDAO = new PostDAO();
        CommentDAO commentDAO = new CommentDAO();
        PostVO post = postDAO.getPostById(postId);
        List<CommentVO> comments = commentDAO.getCommentsByPostId(postId);
        post.setComments(comments);
        if (post == null || !boardName.equals(post.getBoardName())) { // /lounge/아무주소/postId 로 보내는 것을 방지합니다.
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(post, HttpStatus.OK);
    }


}
