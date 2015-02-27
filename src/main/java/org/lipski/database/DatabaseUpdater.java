package org.lipski.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.lipski.controller.WebController;
import org.lipski.place.json.CommentJson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class DatabaseUpdater {

    private static WebController webController = new WebController();

    public static void update() throws IOException {
        Gson gson = new Gson();
        String test = webController.getObjectListToUpdate("comment");
        Type type = new TypeToken<List<CommentJson>>(){}.getType();
        List<CommentJson> commentJsonList = gson.fromJson(test,type);
    }

    private void updateComments(List<CommentJson> comments) {

    }
}
