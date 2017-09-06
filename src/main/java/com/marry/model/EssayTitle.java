package com.marry.model;

import lombok.Data;

import java.util.List;

/**
 * @author ml
 * @create 2017-09-05--17:14
 */

@Data
public class EssayTitle {

    private  String  id;

    private String href;

    private String title;

    private List<Article> articleList;

}
