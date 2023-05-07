package com.kh.moida.vo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PostVO {
    private int postId;
    private int userId;
    private String nickname; // 닉네임은 POST테이블에 있진 않지만 기본적으로 조인해서 사용할 예정입니다
    private String regTime;
    private String title;
    private String contents;
    private int views;
    private int commentsCount;
    private int recommend;
    private String boardName;
    private String imgUrl;


    // insert용 VO입니다
    public PostVO(int userId, String title, String contents, String boardName, String imgUrl) {
        this.userId = userId;
        this.title = title;
        this.contents = contents;
        this.boardName = boardName;
        this.imgUrl = imgUrl;
    }

}


