package com.kh.moida.dao;

import com.kh.moida.util.Common;
import com.kh.moida.vo.PostVO;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PostDAO {
    Connection conn = null;
//    Statement stmt = null;
    PreparedStatement pstmt = null;
    ResultSet rSet = null;


    // 초기 게시물 110개 가져오기
    public List<PostVO> postVOList(String boardName) {
        List<PostVO> list = new ArrayList<>();
        // boardName 체크하는 메소드 있어야겠지?
        // 100개만 가져오기
        try {
            conn = Common.getConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ( SELECT T.*, ROWNUM AS RN FROM ( ");
            sql.append("SELECT P.*, U.NICKNAME FROM POST P INNER JOIN USER_INFO U ");
            sql.append("ON P.USER_ID = U.USER_ID WHERE BOARD_NAME = ? ");
            sql.append("ORDER BY POST_ID DESC) T ) WHERE RN BETWEEN 0 AND 110 ");

            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1,boardName);
            rSet = pstmt.executeQuery();

            while (rSet.next()) {
                int postId = rSet.getInt("POST_ID");
                int userId = rSet.getInt("USER_ID");
                String nickname = rSet.getString("NICKNAME"); // 닉네임은 POST테이블에 있진 않지만 기본적으로 조인해서 사용할 예정입니다
                String regTime = rSet.getString("REG_TIME");
                String title = rSet.getString("TITLE");
                String contents = rSet.getString("CONTENTS");
                int views = rSet.getInt("VIEWS");
                int commentsCount = rSet.getInt("COMMENTS_COUNT");
                int recommend = rSet.getInt("RECOMMEND");
                String imgUrl = rSet.getString("IMG_URL");

                PostVO vo = new PostVO();
                vo.setPostId(postId);
                vo.setUserId(userId);
                vo.setNickname(nickname);
                vo.setRegTime(regTime);
                vo.setTitle(title);
                vo.setContents(contents);
                vo.setViews(views);
                vo.setCommentsCount(commentsCount);
                vo.setRecommend(recommend);
                vo.setImgUrl(imgUrl);
                vo.setBoardName(boardName);

                list.add(vo);
            }
            Common.close(rSet);
            Common.close(pstmt);
            Common.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }

    // lastId 이전의 게시물 100개 가져오기
    public List<PostVO> postVOList(String boardName, int lastId) {
        List<PostVO> list = new ArrayList<>();
        // boardName 체크하는 메소드 있어야겠지?
        // 100개만 가져오기
        try {
            conn = Common.getConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ( SELECT T.*, ROWNUM AS RN FROM ( ");
            sql.append("SELECT P.*, U.NICKNAME FROM POST P INNER JOIN USER_INFO U ");
            sql.append("ON P.USER_ID = U.USER_ID WHERE POST_ID < ? AND BOARD_NAME = ? ");
            sql.append("ORDER BY POST_ID DESC) T ) WHERE RN BETWEEN 0 AND 100 ");

            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setInt(1,lastId);
            pstmt.setString(2,boardName);
            rSet = pstmt.executeQuery();

            while (rSet.next()) {
                int postId = rSet.getInt("POST_ID");
                int userId = rSet.getInt("USER_ID");
                String nickname = rSet.getString("NICKNAME"); // 닉네임은 POST테이블에 있진 않지만 기본적으로 조인해서 사용할 예정입니다
                String regTime = rSet.getString("REG_TIME");
                String title = rSet.getString("TITLE");
                String contents = rSet.getString("CONTENTS");
                int views = rSet.getInt("VIEWS");
                int commentsCount = rSet.getInt("COMMENTS_COUNT");
                int recommend = rSet.getInt("RECOMMEND");
                String imgUrl = rSet.getString("IMG_URL");

                PostVO vo = new PostVO();
                vo.setPostId(postId);
                vo.setUserId(userId);
                vo.setNickname(nickname);
                vo.setRegTime(regTime);
                vo.setTitle(title);
                vo.setContents(contents);
                vo.setViews(views);
                vo.setCommentsCount(commentsCount);
                vo.setRecommend(recommend);
                vo.setImgUrl(imgUrl);
                vo.setBoardName(boardName);

                list.add(vo);
            }
            Common.close(rSet);
            Common.close(pstmt);
            Common.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public PostVO getPostById(int postId) {
        PostVO vo = new PostVO();

        try {
            conn = Common.getConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT P.*, U.NICKNAME FROM POST P INNER JOIN USER_INFO U ");
            sql.append("ON P.USER_ID = U.USER_ID ");
            sql.append("WHERE POST_ID = ? ");

            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setInt(1, postId);
            rSet = pstmt.executeQuery();
            if (rSet.next()) {
                vo.setPostId(postId);
                vo.setUserId(rSet.getInt("USER_ID"));
                vo.setNickname(rSet.getString("NICKNAME"));
                vo.setRegTime(rSet.getString("REG_TIME"));
                vo.setTitle(rSet.getString("TITLE"));
                vo.setContents(rSet.getString("CONTENTS"));
                vo.setViews(rSet.getInt("VIEWS"));
                vo.setCommentsCount(rSet.getInt("COMMENTS_COUNT"));
                vo.setRecommend(rSet.getInt("RECOMMEND"));
                vo.setImgUrl(rSet.getString("IMG_URL"));
                vo.setBoardName(rSet.getString("BOARD_NAME"));
            }
            Common.close(rSet);
            Common.close(pstmt);
            Common.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;

    }


    public boolean postInsert (String userId, String title, String contents, String boardName, String imgUrl) {
        int result = 0;
        String sql = "INSERT INTO POST(USER_ID, TITLE, CONTENTS, BOARD_NAME, IMG_URL) VALUES(?, ?, ?, ?, ?) ";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, userId);
            pstmt.setString(2, title);
            pstmt.setString(3, contents);
            pstmt.setString(4, boardName);
            pstmt.setString(5, imgUrl);

            result = pstmt.executeUpdate();
            System.out.println("post DB 결과 확인 : " + result);


        } catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(pstmt);
        Common.close(conn);

        if(result == 1) return true;
        else return false;
    }

}
