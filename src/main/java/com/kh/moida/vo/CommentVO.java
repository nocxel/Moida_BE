package com.kh.moida.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {
    private int commentId;
    private int userId;
    private int postId;
    private int parentId;
    private String nickname;
    private String regTime;
    private String contents;


    // insert 용 VO
    CommentVO (int userId, int postId, String contents) {
        this.userId = userId;
        this.postId = postId;
        this.contents = contents;
    }

}
