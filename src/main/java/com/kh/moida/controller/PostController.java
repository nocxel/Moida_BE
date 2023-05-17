package com.kh.moida.controller;

import com.kh.moida.dao.CommentDAO;
import com.kh.moida.dao.PostDAO;
import com.kh.moida.vo.CommentVO;
import com.kh.moida.vo.PostVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class PostController {

    /**
     * GET : 게시판에 따른 게시물 조회
     * @param boardName (ex - 'free' , 'qna')
     * @return ResponseEntity<List<PostVO>>
     */
    @GetMapping("/lounge/{boardName}")
    public ResponseEntity<List<PostVO>> postList(@PathVariable String boardName, @RequestParam(value = "lastId", required = false) Integer lastId) {
        System.out.println("BOARD_NAME : " + boardName);
        PostDAO postDAO = new PostDAO();
        List<PostVO> list;
        if (lastId == null) { // lastId가 없는 처음 데이터에는 110개 그 다음 데이터는 lastId값 기준으로 100개씩 보내기
            list = postDAO.postVOList(boardName);
        } else {
            list = postDAO.postVOList(boardName, lastId);
        }
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
        if (post.getPostId() < 1 || !boardName.equals(post.getBoardName())) { // /lounge/아무주소/postId 로 보내는 것을 방지합니다.
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PostMapping("/lounge/{boardName}/write")
    public ResponseEntity<Boolean> memberRegister(@PathVariable String boardName, @RequestBody Map<String, String> regData) {
        String getUserId = regData.get("userId");
        String getTitle = regData.get("title");
        String getContents = regData.get("contents");
        String getImgUrl = regData.get("imgUrl");
        PostDAO dao = new PostDAO();
        boolean isTrue = dao.postInsert(getUserId, getTitle, getContents, boardName, getImgUrl);
        if (isTrue) System.out.println(HttpStatus.OK);
        return new ResponseEntity<>(isTrue, HttpStatus.OK);

    }


}
