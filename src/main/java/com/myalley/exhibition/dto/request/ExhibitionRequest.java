package com.myalley.exhibition.dto.request;

import com.myalley.exhibition.options.ExhibitionStatus;
import com.myalley.exhibition.options.ExhibitionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitionRequest {

    private String title;
    private ExhibitionStatus status;
    private ExhibitionType type;
    private String space;
    private String adultPrice;
    private String fileName;
    private String posterUrl;
    private String date;
    private String webLink;
    private String content;
    private String author;

}
