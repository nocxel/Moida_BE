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
    Statement stmt = null;
    PreparedStatement pstmt = null;
    ResultSet rSet = null;

    /**
     * boardName 넣으면 boardName에 해당하는 게시물리스트 반환
     * @param boardName 보드 이름 (ex '자유', '고민')
     * @return List<PostVO>
     */
    public List<PostVO> postVOList(String boardName) {
        List<PostVO> list = new ArrayList<>();
        // boardName 체크하는 메소드 있어야겠지?
        try {
            conn = Common.getConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT P.*, U.NICKNAME FROM POST P INNER JOIN USER_INFO U ");
            sql.append("ON P.USER_ID = U.USER_ID ");
            sql.append("WHERE P.BOARD_NAME = ? ORDER BY POST_ID DESC ");

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


    public void PostInsert (PostVO vo) {

        try {
            conn = Common.getConnection();
            StringBuilder sql1 = new StringBuilder();
            sql1.append("INSERT INTO POST(USER_ID, TITLE, CONTENTS, BOARD_NAME, IMG_URL) ");
            sql1.append("VALUES (?, ?, ?, ?, ?) ");

            pstmt = conn.prepareStatement(sql1.toString());
            pstmt.setInt(1, vo.getUserId());
            pstmt.setString(2,vo.getTitle());
            pstmt.setString(3, vo.getContents());
            pstmt.setString(4, vo.getBoardName());
            pstmt.setString(5, vo.getImgUrl());

            pstmt.executeUpdate();

            Common.close(pstmt);
            Common.close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
