package com.example.mynote;

//一个用来获取数据的source函数
public class Source {
    private Integer id;
    private String title;
    private String content;
    private String time;
    private String type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {        this.id = id;    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setType(String type){this.type=type;}

    public String getType(){return type;}
    //获取sql语句
    @Override
    public String toString() {
        return "Values{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
