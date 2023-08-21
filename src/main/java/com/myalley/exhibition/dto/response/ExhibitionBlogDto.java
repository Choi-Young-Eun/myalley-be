package com.myalley.exhibition.dto.response;

import com.myalley.exhibition.domain.Exhibition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExhibitionBlogDto {
    private Long id;
    private String title;
    private String posterUrl;
    private String duration;
    private String space;
    private String type;

    public static ExhibitionBlogDto from(Exhibition exhibition){
        return new ExhibitionBlogDto(exhibition.getId(), exhibition.getTitle(), exhibition.getPosterUrl(),
                exhibition.getDuration(), exhibition.getSpace(), exhibition.getType());
    }
}
