package ru.job4j.grabber;

import java.time.LocalDateTime;
import java.util.Objects;

public class Post {

    private int id;
    private String title;
    private String link;
    private String description;
    private LocalDateTime created;

    public Post() {
    }

    public Post(String title, String link, String description, LocalDateTime created) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.created = created;
    }

    @Override
    public String toString() {
        return "Post{" + "id=" + id + ", title='" + title + '\''
                + ", link='" + link + '\'' + ", description='" + description + '\''
                + ", created=" + created + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return title.equals(post.title)
                && link.equals(post.link)
                && description.equals(post.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, link, description);
    }
}
