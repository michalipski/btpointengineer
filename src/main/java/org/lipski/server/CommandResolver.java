package org.lipski.server;

import org.lipski.controller.WebController;
import org.lipski.database.DatabaseAccess;

import java.io.IOException;
import java.sql.SQLException;

public class CommandResolver {

    WebController webController;
    UserQueryProcessor userQueryProcessor;

    public CommandResolver(){
        this.webController = new WebController();
        this.userQueryProcessor = new UserQueryProcessor();
    }

    public Object forwardCommand(String command) throws IOException, SQLException, ClassNotFoundException {
        String commandArray[] = command.split(";",2);
        String commandName = commandArray[0].toUpperCase();
        String commandData = "";
        if (!commandName.equals("LIST")) {
            commandData = commandArray[1];
        }

        switch (Command.valueOf(commandName)) {
            case LOGIN:
                return webController.remoteLogin(commandData);
            case LIST:
                return DatabaseAccess.getPlacesList();
            case GETPLACE:
                return DatabaseAccess.getPlaceData(commandData);
            case COMMENT:
                return webController.commentPlace(commandData);
            case GRADE:
                return webController.gradePlace(commandData);
        }
        return null;
    }

    public enum Command {
        LOGIN,
        LIST,
        GETPLACE,
        COMMENT,
        GRADE

    }
}
