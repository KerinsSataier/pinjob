package ru.pinjob.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Announcement.
 */
@Entity
@Table(name = "announcements")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "announcement")
public class Announcement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 128)
    @Column(name = "title", length = 128, nullable = false)
    private String title;

    @NotNull
    @Size(max = 512)
    @Column(name = "body", length = 512, nullable = false)
    private String body;

    @NotNull
    @Column(name = "date", nullable = false)
    private ZonedDateTime date;

    @Size(max = 1024)
    @Column(name = "place", length = 1024)
    private String place;

    @Column(name = "price")
    private Integer price;

    @Column(name = "bets")
    private Integer bets;

    @Column(name = "expiration")
    private ZonedDateTime expiration;

    @Size(max = 256)
    @Column(name = "picture", length = 256)
    private String picture;

    @Column(name = "pictures")
    private String pictures;

    @Size(max = 256)
    @Column(name = "city", length = 256)
    private String city;

    @ManyToOne
    private User user;

    @ManyToOne
    private Status status;

    @ManyToOne
    private Category category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Announcement title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public Announcement body(String body) {
        this.body = body;
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public Announcement date(ZonedDateTime date) {
        this.date = date;
        return this;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public Announcement place(String place) {
        this.place = place;
        return this;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Integer getPrice() {
        return price;
    }

    public Announcement price(Integer price) {
        this.price = price;
        return this;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getBets() {
        return bets;
    }

    public Announcement bets(Integer bets) {
        this.bets = bets;
        return this;
    }

    public void setBets(Integer bets) {
        this.bets = bets;
    }

    public ZonedDateTime getExpiration() {
        return expiration;
    }

    public Announcement expiration(ZonedDateTime expiration) {
        this.expiration = expiration;
        return this;
    }

    public void setExpiration(ZonedDateTime expiration) {
        this.expiration = expiration;
    }

    public String getPicture() {
        return picture;
    }

    public Announcement picture(String picture) {
        this.picture = picture;
        return this;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPictures() {
        return pictures;
    }

    public Announcement pictures(String pictures) {
        this.pictures = pictures;
        return this;
    }

    public void setPictures(String pictures) {
        this.pictures = pictures;
    }

    public String getCity() {
        return city;
    }

    public Announcement city(String city) {
        this.city = city;
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public User getUser() {
        return user;
    }

    public Announcement user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Status getStatus() {
        return status;
    }

    public Announcement status(Status status) {
        this.status = status;
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public Announcement category(Category category) {
        this.category = category;
        return this;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Announcement announcement = (Announcement) o;
        if(announcement.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, announcement.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Announcement{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", body='" + body + "'" +
            ", date='" + date + "'" +
            ", place='" + place + "'" +
            ", price='" + price + "'" +
            ", bets='" + bets + "'" +
            ", expiration='" + expiration + "'" +
            ", picture='" + picture + "'" +
            ", pictures='" + pictures + "'" +
            ", city='" + city + "'" +
            '}';
    }
}
