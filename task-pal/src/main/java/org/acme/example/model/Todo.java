package org.acme.example.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.util.*;

@Entity
@Table(name = "todo")
public class Todo {

    @Id
    @Column(name = "ID")
    private UUID id;

    @Column(unique = true)
    private String content;

    @Column(name = "completed")
    private boolean completed;

    @Column(name = "ordering")
    private int ordering;

    private String url;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    @CreatedDate
    private Date createdOn;

    public Todo() {
    }

    public Todo(UUID id, String content, boolean completed) {
        this.id = id;
        this.content = content;
        this.completed = completed;
    }

    @Id
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String title) {
        this.content = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getOrdering() {
        return ordering;
    }

    public void setOrdering(int order) {
        this.ordering = order;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}

