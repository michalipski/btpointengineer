package org.lipski.server;

import org.lipski.database.DatabaseAccess;
import org.lipski.place.model.Place;

import java.sql.SQLException;
import java.util.List;

public class UserQueryProcessor {

    public List<String> getPlacesList() throws SQLException, ClassNotFoundException {
        return DatabaseAccess.getPlacesList();
    }

    public Place getPlaceByName(String placeName) throws SQLException, ClassNotFoundException {
        return DatabaseAccess.getPlaceData(placeName);
    }
}
