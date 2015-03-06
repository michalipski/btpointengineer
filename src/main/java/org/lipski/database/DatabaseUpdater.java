package org.lipski.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.lipski.btserver.model.BluetoothServer;
import org.lipski.controller.WebController;
import org.lipski.event.json.EventJson;
import org.lipski.event.model.Event;
import org.lipski.place.json.CommentJson;
import org.lipski.place.json.GradeJson;
import org.lipski.place.json.PlaceJson;
import org.lipski.place.model.Comment;
import org.lipski.place.model.Grade;
import org.lipski.place.model.Place;
import org.lipski.server.ServerProperties;
import org.lipski.users.json.UserJson;
import org.lipski.users.model.User;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class DatabaseUpdater {

    private static WebController webController = new WebController();

    public void update() throws IOException, SQLException, ClassNotFoundException {
        Gson gson = new Gson();
        String jsonString = webController.getUserListToUpdate(DatabaseAccess.getMaxUserId());
        Type type = new TypeToken<List<UserJson>>(){}.getType();
        List<UserJson> userJsonList = gson.fromJson(jsonString,type);
        updateUsers(userJsonList);

        jsonString = webController.getObjectListToUpdate("place");
        type = new TypeToken<List<PlaceJson>>(){}.getType();
        List<PlaceJson> placeJsonList = gson.fromJson(jsonString,type);
        updatePlaces(placeJsonList);

        jsonString = webController.getObjectListToUpdate("comment");
        type = new TypeToken<List<CommentJson>>(){}.getType();
        List<CommentJson> commentJsonList = gson.fromJson(jsonString,type);
        updateComments(commentJsonList);
    }

    private void updateComments(List<CommentJson> comments) {
        if(!comments.isEmpty())
        {
            for (CommentJson commentJson:comments) {
                User user = new User();
                user.setId(commentJson.getUserId());
                Place place = new Place();
                place.setId(commentJson.getPlaceId());
                Comment comment = new Comment(user,place,commentJson.getDescription(),commentJson.getDate());
                comment.setId(commentJson.getId());
                comment.setChanged(false);
                DatabaseAccess<Comment> commentDatabaseAccess = new DatabaseAccess<Comment>();
                commentDatabaseAccess.save(comment);
            }
        }
    }

    private void updateUsers(List<UserJson> users) throws SQLException, ClassNotFoundException {
        if(!users.isEmpty()) {
            for(UserJson userJson:users) {
                User user = new User();
                user.setUsername(userJson.getUsername());
                user.setPassword("");
                user.setEnabled(false);
                user.setId(userJson.getId());
                user.setLastLoginDate(userJson.getLastLogin());
                user.setEmail(userJson.getEmail());
                DatabaseAccess<User> userDatabaseAccess = new DatabaseAccess<User>();
                userDatabaseAccess.save(user);
            }
        }
    }

    private void updatePlaces(List<PlaceJson> places) throws IOException {
        if(!places.isEmpty())
        {
            for (PlaceJson placeJson:places) {
                Place place = new Place(placeJson.getName(),
                        placeJson.getAddress(),
                        placeJson.getCity(),
                        placeJson.getDescription(),
                        placeJson.getPhone(),
                        placeJson.getOpenHour(),
                        placeJson.getCloseHour(),
                        false);
                place.setId(placeJson.getId());
                BluetoothServer bluetoothServer = new BluetoothServer();
                bluetoothServer.setId(Integer.parseInt(ServerProperties.getServerId()));
                place.setBluetoothServer(bluetoothServer);
                DatabaseAccess<Place> placeDatabaseAccess = new DatabaseAccess<Place>();
                placeDatabaseAccess.save(place);
            }
        }
    }

    private void updateEvents(List<EventJson> events) {
        if (!events.isEmpty()) {
            for (EventJson eventJson:events) {
                Place place = new Place();
                place.setId(eventJson.getPlaceId());
                Event event = new Event(eventJson.getName(),place,eventJson.getData(),eventJson.getDescription());
                event.setId(eventJson.getId());
                DatabaseAccess<Event> eventDatabaseAccess = new DatabaseAccess<Event>();
                eventDatabaseAccess.save(event);
            }
        }
    }

    private void updateGrades(List<GradeJson> grades) {
        if (!grades.isEmpty()) {
            for(GradeJson gradeJson:grades) {
                User user = new User();
                user.setId(gradeJson.getUserId());
                Place place = new Place();
                place.setId(gradeJson.getPlaceId());
                Grade grade = new Grade(gradeJson.getGrade(),user,gradeJson.getDate(),place,false);
                grade.setId(gradeJson.getId());
                DatabaseAccess<Grade> gradeDatabaseAccess = new DatabaseAccess<Grade>();
                gradeDatabaseAccess.save(grade);
            }
        }
    }
}
