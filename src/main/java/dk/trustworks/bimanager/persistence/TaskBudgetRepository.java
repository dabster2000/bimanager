package dk.trustworks.bimanager.persistence;

import com.fasterxml.jackson.databind.JsonNode;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hans on 19/05/15.
 */
public class TaskBudgetRepository  {

    public Map<String,Object> findByTaskUUIDAndUserUUID(String taskUUID, String userUUID) {
        Map<String, Object> result = new HashMap<>();
        return result;
    }

    //@Override
    public void create(JsonNode clientJsonNode) throws SQLException {

    }

    //@Override
    public void update(JsonNode clientJsonNode, String uuid) throws SQLException {

    }
}
