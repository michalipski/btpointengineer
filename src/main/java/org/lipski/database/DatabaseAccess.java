package org.lipski.database;

import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.lipski.btserver.model.BluetoothServer;
import org.lipski.event.model.Event;
import org.lipski.photos.model.PlacePhoto;
import org.lipski.place.model.Comment;
import org.lipski.place.model.Grade;
import org.lipski.place.model.Place;
import org.lipski.users.json.UserJson;
import org.lipski.users.model.User;

import javax.imageio.ImageIO;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseAccess<T> {

    public DatabaseAccess() {}

    private static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/btsdb","root","MgE10UgC");
    }

    public static ArrayList<String> getPlacesList() {
        Session session = HibernateUtils.sessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        List<String> placesList = session.createCriteria(Place.class)
                .setProjection(Projections.property("name"))
                .list();
        transaction.commit();
        ArrayList<String> placesArrayList = new ArrayList<String>(placesList);
        return placesArrayList;
    }

    public static Place getPlaceData(String placeName) {
        Session session = HibernateUtils.sessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        Place place = (Place) session.createCriteria(Place.class)
                .add(Restrictions.like("name", placeName))
                .setProjection(Projections.projectionList()
                                .add(Projections.property("id"), "id")
                                .add(Projections.property("name"), "name")
                                .add(Projections.property("address"),"address")
                                .add(Projections.property("city"),"city")
                                .add(Projections.property("description"),"description")
                                .add(Projections.property("phone"),"phone")
                                .add(Projections.property("openHour"),"openHour")
                                .add(Projections.property("closeHour"),"closeHour")
                ).setResultTransformer(Transformers.aliasToBean(Place.class))
                .uniqueResult();
        transaction.commit();

        List<Comment> commentList = getCommentsForPlace(place.getId());
        ArrayList<Comment> comments = new ArrayList<Comment>(commentList);

        return Place.getPlaceForBluetoothCommunication(place,comments);
    }

    private static List getCommentsForPlace(Integer placeId) {
        Session session = HibernateUtils.sessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        List<Comment> comments = session.createCriteria(Comment.class)
                .add(Restrictions.eq("place.id", placeId))
                .setProjection(Projections.projectionList()
                        .add(Projections.property("id"),"id")
                        .add(Projections.property("description"),"description")
                        .add(Projections.property("date"),"date")
                ).setResultTransformer(Transformers.aliasToBean(Comment.class))
                .list();
        transaction.commit();
        return comments;
    }

    public void save(T object) {
        Session session = HibernateUtils.sessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.replicate(object, ReplicationMode.EXCEPTION);
        transaction.commit();
    }

    public static Integer getMaxUserId() throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        String query = "SELECT MAX(user_id) FROM users";
        ResultSet result = statement.executeQuery(query);
        Integer maxId = 0;
        while (result.next()) {
            maxId = result.getInt(1);
        }
        if(maxId==null) {
            return 0;
        } else {
            return maxId;
        }
    }

    public void saveUser(UserJson userJson) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        String query = "INSERT INTO users (user_id,username) VALUES(" + userJson.getId().toString() + ",'" + userJson.getUsername() + "')";
        statement.execute(query);
    }

    public static ArrayList<String> getEventsList() {
        Session session = HibernateUtils.sessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        List<String> eventsList = session.createCriteria(Event.class)
                .setProjection(Projections.property("name"))
                .list();
        transaction.commit();
        ArrayList<String> eventsArrayList = new ArrayList<String>(eventsList);
        return eventsArrayList;
    }

    public static Map<String, Object> getEventData(String commandData) {
        Map<String,Object> objectsToSend = new HashMap<String, Object>();

        Session session = HibernateUtils.sessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        Event event = (Event) session.createCriteria(Event.class)
                .add(Restrictions.like("name",commandData))
                .setProjection(Projections.projectionList()
                    .add(Projections.property("id"),"id")
                    .add(Projections.property("name"),"name")
                    .add(Projections.property("data"),"data"))
                .setResultTransformer(Transformers.aliasToBean(Event.class))
                .uniqueResult();

        String placeName = (String) session.createCriteria(Event.class,"event")
                .add(Restrictions.like("name",commandData))
                .createAlias("event.place","place")
                .setProjection(Projections.property("place.name"))
                .uniqueResult();
        transaction.commit();
        Place place = getPlaceData(placeName);
        objectsToSend.put("event",event);
        objectsToSend.put("place",place);

        return objectsToSend;
    }

    private static byte[] getByteArrayPhoto(PlacePhoto photo) throws IOException {
        String photoDir = photo.getPhotoDir();
        File photoFile = new File(photoDir);
        BufferedImage image = ImageIO.read(photoFile);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image,"jpg",byteArrayOutputStream);
        byteArrayOutputStream.flush();
        byte[] resultPhoto = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return resultPhoto;
    }

    public static ArrayList<byte[]> getPhotosList(String placeName) throws IOException {
        ArrayList<byte[]> photosArray = new ArrayList<byte[]>();
        List<PlacePhoto> photos = getPhotosForPlace(placeName);
        for(PlacePhoto photo:photos) {
            photosArray.add(getByteArrayPhoto(photo));
        }
        return photosArray;
    }

    private static List<PlacePhoto> getPhotosForPlace(String placeName) {
        Session session = HibernateUtils.sessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        List<PlacePhoto> photosList = session.createCriteria(PlacePhoto.class,"photo")
                .createAlias("photo.place","place")
                .add(Restrictions.like("place.name",placeName))
                .list();
        transaction.commit();
        return photosList;
    }

//    public static ArrayList<String> getPlacesList() throws SQLException, ClassNotFoundException {
//        Connection connection = getConnection();
//        Statement statement = connection.createStatement();
//        String query = "SELECT name FROM places";
//        ResultSet result = statement.executeQuery(query);
//        ArrayList<String> placesList = new ArrayList<String>();
//        while (result.next()) {
//            placesList.add(result.getString("name"));
//        }
//        return placesList;
//    }

//    public static Place getPlaceData(String placeName) throws SQLException, ClassNotFoundException {
//        Connection connection = getConnection();
//        Statement statement = connection.createStatement();
//        String query = String.format("SELECT * FROM places WHERE name = \'%s\'", placeName);
//        ResultSet result = statement.executeQuery(query);
//
//        Place place;
//
//        if(result.next()) {
//            place = new Place(
//                    result.getString("name"),
//                    result.getString("address"),
//                    result.getString("city"),
//                    result.getString("description"),
//                    result.getInt("phone"),
//                    result.getTimestamp("open_hour"),
//                    result.getTimestamp("close_hour"));
//
//        } else {
//            place = new Place();
//        }
//
//        return place;
//    }
}
