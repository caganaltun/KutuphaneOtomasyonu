package cagan.model;

public class Book {
    private int id;
    private String title;
    private String author;
    private String category;
    private int PageCount;

    public Book(int id, String title, String author, String category,  int PageCount) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.PageCount = PageCount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getPageCount() {return PageCount; }
    public void setPageCount(int PageCount) {this.PageCount = PageCount;}
}