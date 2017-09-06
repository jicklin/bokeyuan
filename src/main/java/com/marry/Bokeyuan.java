package com.marry;

import com.itextpdf.text.*;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.lowagie.text.*;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.fonts.cmaps.CodespaceRange;
import com.lowagie.text.rtf.RtfWriter2;
import com.marry.model.Article;
import com.marry.model.EssayTitle;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.swing.text.html.HTML.Tag.HTML;


/**
 * @author ml
 * @create 2017-09-05--16:18
 */
public class Bokeyuan {

    List<EssayTitle> essayTitleList=new ArrayList<EssayTitle>();

    private final static String bathpath="F:\\gacl\\";


    private Rectangle pageSize=new Rectangle(PageSize.A4);


    public void getHtml(String url) throws IOException {

        Document doc = getdocbyHref(url);


        //获取随笔分类的列表
        Elements essayTitles = doc.select("#sidebar_categories ul:eq(1) li a");

        EssayTitle essayTitle = null;
        for (Element e : essayTitles) {
            essayTitle=new EssayTitle();
            essayTitle.setId(e.attr("id"));
            essayTitle.setHref(e.attr("href"));
            essayTitle.setTitle(e.text());
            System.out.println("essayTitle = " + essayTitle);
            essayTitleList.add(essayTitle);
        }

        System.out.println(essayTitleList.size());




    }

    protected void getArticles() throws IOException {

        for(EssayTitle essayTitle:essayTitleList){
            String href=essayTitle.getHref();
            Document document=getdocbyHref(href);


            Elements links=document.select("div.post h5 a");
            Article article=null;
            List<Article> articleList=null;
            articleList=new ArrayList<Article>();
            for(Element e:links){
                if(e==null) {
                    continue;
                }
                article=new Article();
                String articleurl=e.attr("href");
                Document articledoc=getdocbyHref(articleurl);

                //文章标题
                Element articleTitle=articledoc.select("#cb_post_title_url").first();
                if(articleTitle==null){
                    continue;
                }
                article.setTitle(articleTitle.text());

                //文章内容
                Element articleContent=articledoc.select("#cnblogs_post_body").first();
                //查看有无base64位编码的图片
                articledoc.select("img[src*=base64]").attr("src","");
                article.setHref(articleurl);
                article.setContent(articledoc.toString());

                articleList.add(article);



            }
            essayTitle.setArticleList(articleList);
        }


    }

    protected void saveArticles() throws IOException, DocumentException, com.itextpdf.text.DocumentException {

        File bathdirectory=new File(bathpath);
        if(!bathdirectory.exists()){
            bathdirectory.mkdir();
        }

        for(EssayTitle essayTitle:essayTitleList){

            String secondDirectoryName=bathpath+essayTitle.getTitle()+"\\";

            File secondDirectory=new File(secondDirectoryName);
            if(!secondDirectory.exists()){
                secondDirectory.mkdirs();
            }
            List<Article> articles=essayTitle.getArticleList();
            if(articles==null){
                continue;
            }
            for(Article article:articles){
               /* if(article.getTitle().contains("Android开发学习总结（六）—— APK反编译") || article.getTitle().contains("appcompat_v7项目说明") || article.getTitle().contains("在Web应用中嵌入H2数据库") || article.getTitle().contains("Hessian学习总结(一)——Hessian入门")){
                    continue;
                }*/


                createDoc(article,secondDirectoryName);

            }





        }


    }

    /*生成doc文档*/
    private void createDoc(Article article, String secondDirectoryName) throws IOException, DocumentException, com.itextpdf.text.DocumentException {
        String title=article.getTitle();
        //判断是否有特殊字符
        Pattern pattern=Pattern.compile("[\\s\\/:\\*\\?\\\"<>\\|]");
        Matcher matcher=pattern.matcher(title);
        while (matcher.find()){
            title=matcher.replaceAll("");
        }
        /*doc
        //判断文件是否存在
        System.out.println(secondDirectoryName+article.getTitle()+".doc");
        if(!(new File(secondDirectoryName+title+".doc").exists())){
            new File(secondDirectoryName+title+".doc").createNewFile();
        }

        File file=new File(secondDirectoryName+title+".doc");

        if(!(file.canRead() && file.canWrite())){
            System.out.println("不具备文档操作权限");
            file.setReadable(true);
            file.setWritable(true);
        }

        pageSize=pageSize.rotate();

        com.lowagie.text.Document doc=new com.lowagie.text.Document(pageSize,80,80,50,50);

        //创建一个word文档读写器
        com.lowagie.text.rtf.RtfWriter2.getInstance(doc,new FileOutputStream(secondDirectoryName+title+".doc"));
        doc.open();

        //设置文章标题
        Paragraph titlePara=new Paragraph(article.getTitle(),new Font(Font.NORMAL,18,Font.BOLD,new Color(0,0,0)));

        titlePara.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);

        doc.add(titlePara);



        if(article.getContent().contains("E:")){
            return;
        }

        //书写内容
        StyleSheet ss=new StyleSheet();
        List htmlList= HTMLWorker.parseToList(new StringReader(article.getContent()),ss);

        for(int i=0;i<htmlList.size();i++){
            com.lowagie.text.Element e= (com.lowagie.text.Element) htmlList.get(i);
            Paragraph par=new Paragraph();
            par.add(e);
            doc.add(par);

        }

        doc.close();*/

        /* html
        File pdfFile=new File(secondDirectoryName+title+".pdf");

        // step 1
        com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
        // step 2
        com.itextpdf.text.pdf.PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(doc, new FileOutputStream(pdfFile));
        // step 3
        doc.open();
        // step 4
        InputStream htmlInput = new ByteArrayInputStream(article.getContent().getBytes("UTF-8"));
        XMLWorkerHelper. getInstance().parseXHtml(writer, doc, htmlInput);
        // step 5
        doc.close();*/

        BufferedInputStream bin=null;
        FileOutputStream fout=null;
        BufferedOutputStream bout=null;

        try {
            ByteArrayInputStream inputStream=new ByteArrayInputStream(article.getContent().getBytes());
            bin=new BufferedInputStream(inputStream);
            File htmlFile=new File(secondDirectoryName+title+".html");

            fout=new FileOutputStream(htmlFile);

            bout=new BufferedOutputStream(fout);

            byte[] buffers=new byte[1024];

            int len=-1;

            while ((len=bin.read(buffers))!=-1){
                bout.write(buffers,0,len);
            }

            bout.flush();
        } catch (IOException e) {
            throw e;
        } finally {

            if(bin!=null)
                bin.close();
            if(fout!=null)
                fout.close();
            if(bout!=null)
                bout.close();
        }


    }


    protected Document getdocbyHref(String url) throws IOException {
        Document doc = Jsoup.connect(url).timeout(10000).get();

        return doc;
    }

    public static void main(String[] args) {
        Bokeyuan bokeyuan = new Bokeyuan();

        try {
            bokeyuan.getHtml("http://www.cnblogs.com/xdp-gacl/mvc/blog/sidecolumn.aspx?blogApp=xdp-gacl");
            bokeyuan.getArticles();
            bokeyuan.saveArticles();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (com.itextpdf.text.DocumentException e) {
            e.printStackTrace();
        }
    }
}
