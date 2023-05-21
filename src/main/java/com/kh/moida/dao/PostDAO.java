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


    // 초기 게시물 110개 가져오기
    public List<PostVO> postVOList(String boardName) {
        List<PostVO> list = new ArrayList<>();
        try {
            conn = Common.getConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ( SELECT T.*, ROWNUM AS RN FROM ( ");
            sql.append("SELECT P.*, U.NICKNAME FROM POST P INNER JOIN USER_INFO U ");
            sql.append("ON P.USER_ID = U.USER_ID WHERE BOARD_NAME = ? ");
            sql.append("ORDER BY POST_ID DESC) T ) WHERE RN BETWEEN 0 AND 110 ");

            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, boardName);
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
            pstmt.setInt(1, lastId);
            pstmt.setString(2, boardName);
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


    // 게시믈 조회 + 조회했을 때 조회 수를 증가시킵니다.
    public PostVO getPostById(int postId) {
        PostVO vo = new PostVO();

        // 게시물 조회 쿼리문
        StringBuilder sql1 = new StringBuilder();
        sql1.append("SELECT P.*, U.NICKNAME, U.IMG_URL AS USER_IMG_URL FROM POST P INNER JOIN USER_INFO U ");
        sql1.append("ON P.USER_ID = U.USER_ID ");
        sql1.append("WHERE POST_ID = ? ");

        // 조회 수 증가 쿼리문
        String sql2 = "UPDATE POST SET VIEWS = VIEWS + 1 WHERE POST_ID = ? ";

        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql1.toString());
            pstmt.setInt(1, postId);
            rSet = pstmt.executeQuery();
            if (rSet.next()) {
                vo.setPostId(postId);
                vo.setUserId(rSet.getInt("USER_ID"));
                vo.setNickname(rSet.getString("NICKNAME"));
                vo.setUserImgUrl(rSet.getString("USER_IMG_URL"));
                vo.setRegTime(rSet.getString("REG_TIME"));
                vo.setTitle(rSet.getString("TITLE"));
                vo.setContents(rSet.getString("CONTENTS"));
                vo.setViews(rSet.getInt("VIEWS"));
                vo.setCommentsCount(rSet.getInt("COMMENTS_COUNT"));
                vo.setRecommend(rSet.getInt("RECOMMEND"));
                vo.setImgUrl(rSet.getString("IMG_URL"));
                vo.setBoardName(rSet.getString("BOARD_NAME"));
            }
            pstmt = conn.prepareStatement(sql2);
            pstmt.setInt(1, postId);
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(rSet);
        Common.close(pstmt);
        Common.close(conn);
        return vo;
    }


    // 게시물 등록
    public boolean postInsert(int userId, String title, String contents, String boardName, String imgUrl) {
        int result = 0;
        String sql = "INSERT INTO POST(USER_ID, TITLE, CONTENTS, BOARD_NAME, IMG_URL) VALUES(?, ?, ?, ?, ?) ";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, userId);
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

        if (result == 1) return true;
        else return false;
    }

    //     게시물 수정
    public boolean postUpdate(String title, String contents, int postId) {
        int result = 0;
        String sql = "UPDATE POST SET TITLE = ? CONTENTS= ? WHERE POST_ID = ? ";
        try {
            conn = Common.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, title);
            pstmt.setString(3, contents);
            pstmt.setInt(3, postId);

            result = pstmt.executeUpdate();
            System.out.println("post DB 결과 확인 : " + result);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(pstmt);
        Common.close(conn);

        if (result == 1) return true;
        else return false;
    }

    // 게시물 삭제
    public boolean postDelete(int postId) {
        int result = 0;
        String sql = "DELETE FROM POST WHERE POST_ID = " + postId;
        try {
            conn = Common.getConnection();
            stmt = conn.createStatement();

            result = stmt.executeUpdate(sql);
            System.out.println("post DB 결과 확인 : " + result);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Common.close(stmt);
        Common.close(conn);

        if (result == 1) return true;
        else return false;
    }


    // 게시물 추천
    public boolean recommendPost(int userId, int postId) {
        int result = 0;
        String sql1 = "INSERT INTO POST_RECOMMEND(USER_ID, POST_ID) VALUES (?, ?) ";
        String sql2 = "UPDATE POST SET RECOMMEND = RECOMMEND + 1 WHERE POST_ID = ? ";

        try {
            conn = Common.getConnection();
            conn.setAutoCommit(false); // 쿼리문이 1개만 성공할 때 자동 커밋을 막기 위함
            pstmt = conn.prepareStatement(sql1);

            pstmt.setInt(1, userId);
            pstmt.setInt(2, postId);
            result = pstmt.executeUpdate();
            System.out.println("post DB 결과 확인 : " + result);

            if (result == 0) {
                pstmt = conn.prepareStatement(sql2);
                pstmt.setInt(1, postId);
                result = pstmt.executeUpdate();
                conn.commit();
            } else {
                conn.rollback();
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 에러 발생 시 rollback해줍니다.
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        Common.close(pstmt);
        Common.close(conn);

        if (result == 1) return true;
        else return false;
    }

    // 게시물 추천 취소
    public boolean undoRecommendPost(int userId, int postId) {
        int result = 0;
        String sql1 = "DELETE FROM POST_RECOMMEND WHERE USER_ID = ? AND POST_ID = ? ";
        String sql2 = "UPDATE POST SET RECOMMEND = RECOMMEND - 1 WHERE POST_ID = ? ";

        try {
            conn = Common.getConnection();
            conn.setAutoCommit(false); // 쿼리문이 1개만 성공할 때 자동 커밋을 막기 위함
            pstmt = conn.prepareStatement(sql1);

            pstmt.setInt(1, userId);
            pstmt.setInt(2, postId);
            result = pstmt.executeUpdate();
            System.out.println("post DB 결과 확인 : " + result);

            if (result == 0) {
                pstmt = conn.prepareStatement(sql2);
                pstmt.setInt(1, postId);
                result = pstmt.executeUpdate();
                conn.commit();
            } else {
                conn.rollback();
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 에러 발생 시 rollback해줍니다.
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        Common.close(pstmt);
        Common.close(conn);

        if (result == 1) return true;
        else return false;
    }

}
