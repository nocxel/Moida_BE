package com.kh.moida.dao;

import org.springframework.stereotype.Repository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.kh.moida.util.Common;
import com.kh.moida.vo.CommentVO;


@Repository
public class CommentDAO {
    Connection conn = null;
    Statement stmt = null;
    PreparedStatement pstmt = null;
    ResultSet rSet = null;


    public List<CommentVO> getCommentsByPostId(int postId) {
        List<CommentVO> list = new ArrayList<>();

        try {
            conn = Common.getConnection();
            StringBuilder sql = new StringBuilder();

            sql.append("SELECT P.*, U.NICKNAME, U.IMG_URL FROM POST_COMMENT P INNER JOIN USER_INFO U ");
            sql.append("ON P.USER_ID = U.USER_ID WHERE POST_ID = ? ");
            sql.append("START WITH PARENT_COMMENT_ID IS NULL ");
            sql.append("CONNECT BY PRIOR POST_COMMENT_ID = PARENT_COMMENT_ID ");

            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, String.valueOf(postId));
            rSet = pstmt.executeQuery();

            while (rSet.next()) {
                int commentId = rSet.getInt("POST_COMMENT_ID");
                int userId = rSet.getInt("USER_ID");
                int parentId = rSet.getInt("PARENT_COMMENT_ID");
                String nickname = rSet.getString("NICKNAME"); // 닉네임은 POST테이블에 있진 않지만 기본적으로 조인해서 사용할 예정입니다
                String imgUrl = rSet.getString("IMG_URL");
                String regTime = rSet.getString("REG_TIME");
                String contents = rSet.getString("CONTENTS");

                CommentVO vo = new CommentVO();
                vo.setCommentId(commentId);
                vo.setUserId(userId);
                vo.setParentId(parentId);
                vo.setPostId(postId);
                vo.setNickname(nickname);
                vo.setImgUrl(imgUrl);
                vo.setRegTime(regTime);
                vo.setContents(contents);

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

}
