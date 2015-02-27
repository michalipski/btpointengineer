package org.lipski.database;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.lipski.btserver.model.BluetoothServer;
import org.lipski.event.model.Event;
import org.lipski.place.model.Comment;
import org.lipski.place.model.Grade;
import org.lipski.place.model.Place;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/engineerdb","root","MgE10UgC");
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
