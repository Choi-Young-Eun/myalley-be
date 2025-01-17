package com.myalley.member.dto;

import com.myalley.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberBlogDto {
    private Long memberId;
    private String nickname;
    private String memberImage;

    public static MemberBlogDto from(Member member){
        return new MemberBlogDto(member.getMemberId(), member.getNickname(), member.getMemberImage());
    }
}
