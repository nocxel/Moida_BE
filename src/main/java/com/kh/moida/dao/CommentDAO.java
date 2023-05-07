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

    public List<CommentVO> commentVOList(int postId) {
        List<CommentVO> list = new ArrayList<>();
        // boardName 체크하는 메소드 있어야겠지?
        try {
            conn = Common.getConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT C.*, U.NICKNAME FROM COMMENTS C INNER JOIN USER_INFO U ");
            sql.append("ON C.USER_ID = U.USER_ID ");
            sql.append("WHERE POST_ID = ?  ");

            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, String.valueOf(postId));
            rSet = pstmt.executeQuery();

            while (rSet.next()) {
                int commentId = rSet.getInt("COMMENT_ID");
                int userId = rSet.getInt("USER_ID");
                int parentId = rSet.getInt("PARENT_COMMENT_ID");
                String nickname = rSet.getString("NICKNAME"); // 닉네임은 POST테이블에 있진 않지만 기본적으로 조인해서 사용할 예정입니다
                String regTime = rSet.getString("REG_TIME");
                String contents = rSet.getString("CONTENTS");

                CommentVO vo = new CommentVO();
                vo.setCommentId(commentId);
                vo.setUserId(userId);
                vo.setParentId(parentId);
                vo.setPostId(postId);
                vo.setNickname(nickname);
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
