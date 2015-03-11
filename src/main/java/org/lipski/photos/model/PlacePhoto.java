package org.lipski.photos.model;

import org.lipski.place.model.Place;

import javax.persistence.*;

@Entity
@Table(name = "photos")
public class PlacePhoto {

    @Id
    @Column(name = "id_photo")
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    Place place;

    @Column(name = "photo_dir")
    String photoDir;

    public PlacePhoto() {
    }

    public PlacePhoto(Place place, String photo) {
        this.place = place;
        this.photoDir = photo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public String getPhotoDir() {
        return photoDir;
    }

    public void setPhotoDir(String photoDir) {
        this.photoDir = photoDir;
    }
}
