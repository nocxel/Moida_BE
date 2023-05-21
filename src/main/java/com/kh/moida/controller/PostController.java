package com.kh.moida.controller;

import com.kh.moida.dao.CommentDAO;
import com.kh.moida.dao.PostDAO;
import com.kh.moida.vo.CommentVO;
import com.kh.moida.vo.PostVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // post insert
    @PostMapping("/lounge/post/insert")
    public ResponseEntity<Boolean> postRegister(@RequestBody Map<String, String> regData) {
        int getUserId = Integer.parseInt(regData.get("userId"));
        String getTitle = regData.get("title");
        String getContents = regData.get("contents");
        String getBoardName = regData.get("boardName");
        String getImgUrl = regData.get("imgUrl");
        PostDAO dao = new PostDAO();
        boolean Result = dao.postInsert(getUserId, getTitle, getContents, getBoardName, getImgUrl);
        if (Result) System.out.println(HttpStatus.OK);
        return new ResponseEntity<>(Result, HttpStatus.OK);
    }
    // post update
    @PostMapping("/lounge/post/update")
    public ResponseEntity<Boolean> postModifier(@RequestBody Map<String, String> modData) {
        String getTitle = modData.get("title");
        String getContents = modData.get("contents");
        int getPostId = Integer.parseInt(modData.get("postId"));
        PostDAO dao = new PostDAO();
        boolean result = dao.postUpdate(getTitle, getContents, getPostId);
        if (result) System.out.println(HttpStatus.OK);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    // post recommned
    @PostMapping("/lounge/post/recommend")
    public ResponseEntity<Boolean> postRecommender (@RequestBody Map<String, String> data) {
        int getUserId = Integer.parseInt(data.get("userId"));
        int getPostId = Integer.parseInt(data.get("postId"));
        PostDAO dao = new PostDAO();
        boolean result = dao.recommendPost(getUserId, getPostId);
        if (result) System.out.println(HttpStatus.OK);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    // post undoRecommend
    @PostMapping("/lounge/post/undoRecommend")
    public ResponseEntity<Boolean> postUndoRecommender (@RequestBody Map<String, String> data) {
        int getUserId = Integer.parseInt(data.get("userId"));
        int getPostId = Integer.parseInt(data.get("postId"));
        PostDAO dao = new PostDAO();
        boolean result = dao.undoRecommendPost(getUserId, getPostId);
        if (result) System.out.println(HttpStatus.OK);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    // comment insert
    @PostMapping("/lounge/comment/insert")
    public ResponseEntity<Boolean> commentRegister(@RequestBody Map<String, String> regData) {
        int getUserId = Integer.parseInt(regData.get("userId"));
        int getPostId = Integer.parseInt(regData.get("postId"));
        int getParentId = regData.get("parentId") != null ? Integer.parseInt(regData.get("parentId")) : 0;
        String getContents = regData.get("contents");
        CommentDAO dao = new CommentDAO();
        boolean insertResult;
        if (getParentId < 1) {
            insertResult = dao.postCommentInsert(getUserId, getPostId, getContents);
        } else { // parentId 가 있을 때 ( 대댓글 등록 )
            insertResult = dao.postCommentInsert(getUserId, getPostId, getParentId, getContents);
        }
        if (insertResult) System.out.println(HttpStatus.OK);
        return new ResponseEntity<>(insertResult, HttpStatus.OK);
    }

    @PostMapping("/lounge/comment/update")
    public ResponseEntity<Boolean> commentModifier(@RequestBody Map<String, String> modData) {
        int getCommentId = Integer.parseInt(modData.get("commentId"));
        String getContents = modData.get("contents");
        CommentDAO dao = new CommentDAO();
        boolean updateResult = dao.postCommentUpdate(getCommentId, getContents);
        if (updateResult) System.out.println(HttpStatus.OK);
        return new ResponseEntity<>(updateResult, HttpStatus.OK);
    }



}
