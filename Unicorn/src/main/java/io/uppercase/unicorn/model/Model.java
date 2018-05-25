package io.uppercase.unicorn.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.uppercase.unicorn.handler.MethodHandler;
import io.uppercase.unicorn.model.handler.CountHandler;
import io.uppercase.unicorn.model.handler.CreateHandler;
import io.uppercase.unicorn.model.handler.ExistsHandler;
import io.uppercase.unicorn.model.handler.FindHandler;
import io.uppercase.unicorn.model.handler.GetHandler;
import io.uppercase.unicorn.model.handler.OnNewHandler;
import io.uppercase.unicorn.model.handler.OnRemoveHandler;
import io.uppercase.unicorn.model.handler.RemoveHandler;
import io.uppercase.unicorn.model.handler.UpdateHandler;
import io.uppercase.unicorn.room.Room;
import io.uppercase.unicorn.room.RoomServerConnector;

public class Model {

    private RoomServerConnector connector;
    private String boxName;
    private String name;

    private Room room;

    /**
     * @param connector
     * @param boxName
     * @param name
     */
    public Model(RoomServerConnector connector, String boxName, String name) {

        this.connector = connector;
        this.boxName = boxName;
        this.name = name;

        room = new Room(connector, boxName, name);
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return room
     */
    public Room getRoom() {
        return room;
    }

    /**
     * @param data
     * @param handler
     */
    public void create(JSONObject data, final CreateHandler handler) {

        room.send("create", data, new MethodHandler() {

            @Override
            public void handle(Object data) {
                try {

                    JSONObject result = (JSONObject) data;

                    CreateHandler h = handler;
                    if (h == null) {
                        h = new CreateHandler() {
                        };
                    }

                    if (result.isNull("errorMsg") != true) {
                        h.error(result.getString("errorMsg"));
                    } else if (result.isNull("validErrors") != true) {
                        h.notValid(result.getJSONObject("validErrors"));
                    } else if (result.isNull("isNotAuthed") != true && result.getBoolean("isNotAuthed") == true) {
                        h.notAuthed();
                    } else {
                        h.success(result.getJSONObject("savedData"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @param data
     */
    public void create(JSONObject data) {
        create(data, null);
    }

    /**
     * @param id
     * @param handler
     */
    public void get(String id, final GetHandler handler) {

        room.send("get", id, new MethodHandler() {

            @Override
            public void handle(Object data) {
                try {

                    JSONObject result = (JSONObject) data;

                    if (result.isNull("errorMsg") != true) {
                        handler.error(result.getString("errorMsg"));
                    } else if (result.isNull("isNotAuthed") != true && result.getBoolean("isNotAuthed") == true) {
                        handler.notAuthed();
                    } else if (result.isNull("savedData") == true) {
                        handler.notExists();
                    } else {
                        handler.success(result.getJSONObject("savedData"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @param filter
     * @param sort
     * @param isRandom
     * @param handler
     */
    public void get(JSONObject filter, JSONObject sort, Boolean isRandom, final GetHandler handler) {

        JSONObject params = new JSONObject();

        try {
            if (filter != null) {
                params.put("filter", filter);
            }
            if (sort != null) {
                params.put("sort", sort);
            }
            if (isRandom != null) {
                params.put("isRandom", isRandom);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        room.send("get", params, new MethodHandler() {

            @Override
            public void handle(Object data) {
                try {

                    JSONObject result = (JSONObject) data;

                    if (result.isNull("errorMsg") != true) {
                        handler.error(result.getString("errorMsg"));
                    } else if (result.isNull("isNotAuthed") != true && result.getBoolean("isNotAuthed") == true) {
                        handler.notAuthed();
                    } else if (result.isNull("savedData") == true) {
                        handler.notExists();
                    } else {
                        handler.success(result.getJSONObject("savedData"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @param filter
     * @param sort
     * @param handler
     */
    public void get(JSONObject filter, JSONObject sort, GetHandler handler) {
        get(filter, sort, null, handler);
    }

    /**
     * @param filter
     * @param handler
     */
    public void get(JSONObject filter, GetHandler handler) {
        get(filter, null, handler);
    }

    /**
     * @param data
     * @param handler
     */
    public void update(JSONObject data, final UpdateHandler handler) {

        room.send("update", data, new MethodHandler() {

            @Override
            public void handle(Object data) {
                try {

                    JSONObject result = (JSONObject) data;

                    UpdateHandler h = handler;
                    if (h == null) {
                        h = new UpdateHandler() {};
                    }

                    if (result.isNull("errorMsg") != true) {
                        h.error(result.getString("errorMsg"));
                    } else if (result.isNull("validErrors") != true) {
                        h.notValid(result.getJSONObject("validErrors"));
                    } else if (result.isNull("isNotAuthed") != true && result.getBoolean("isNotAuthed") == true) {
                        h.notAuthed();
                    } else if (result.isNull("savedData") == true) {
                        h.notExists();
                    } else {
                        h.success(result.getJSONObject("savedData"), result.getJSONObject("originData"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @param id
     * @param handler
     */
    public void remove(String id, final RemoveHandler handler) {

        room.send("remove", id, new MethodHandler() {

            @Override
            public void handle(Object data) {
                try {

                    JSONObject result = (JSONObject) data;

                    RemoveHandler h = handler;
                    if (h == null) {
                        h = new RemoveHandler() {};
                    }

                    if (result.isNull("errorMsg") != true) {
                        h.error(result.getString("errorMsg"));
                    } else if (result.isNull("isNotAuthed") != true && result.getBoolean("isNotAuthed") == true) {
                        h.notAuthed();
                    } else if (result.isNull("originData") == true) {
                        h.notExists();
                    } else {
                        h.success(result.getJSONObject("originData"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @param filter
     * @param sort
     * @param start
     * @param count
     * @param handler
     */
    public void find(JSONObject filter, JSONObject sort, long start, int count, final FindHandler handler) {

        JSONObject params = new JSONObject();

        try {
            if (filter != null) {
                params.put("filter", filter);
            }
            if (sort != null) {
                params.put("sort", sort);
            }
            if (start != -1) {
                params.put("start", start);
            }
            if (count != -1) {
                params.put("count", count);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        room.send("find", params, new MethodHandler() {

            @Override
            public void handle(Object data) {
                try {

                    JSONObject result = (JSONObject) data;

                    if (result.isNull("errorMsg") != true) {
                        handler.error(result.getString("errorMsg"));
                    } else if (result.isNull("isNotAuthed") != true && result.getBoolean("isNotAuthed") == true) {
                        handler.notAuthed();
                    } else {

                        List<JSONObject> savedDataSet = new ArrayList<JSONObject>();

                        JSONArray jsonArray = result.getJSONArray("savedDataSet");

                        for (int i = 0; i < jsonArray.length(); i += 1) {
                            savedDataSet.add((JSONObject) jsonArray.get(i));
                        }

                        handler.success(savedDataSet);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @param filter
     * @param sort
     * @param count
     * @param handler
     */
    public void find(JSONObject filter, JSONObject sort, int count, FindHandler handler) {
        find(filter, sort, -1, count, handler);
    }

    /**
     * @param filter
     * @param sort
     * @param handler
     */
    public void find(JSONObject filter, JSONObject sort, FindHandler handler) {
        find(filter, sort, -1, handler);
    }

    /**
     * @param filter
     * @param handler
     */
    public void find(JSONObject filter, FindHandler handler) {
        find(filter, null, handler);
    }

    /**
     * @param handler
     */
    public void find(FindHandler handler) {
        find(null, handler);
    }

    /**
     * @param filter
     * @param handler
     */
    public void count(JSONObject filter, final CountHandler handler) {

        JSONObject params = new JSONObject();

        try {
            if (filter != null) {
                params.put("filter", filter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        room.send("count", params, new MethodHandler() {

            @Override
            public void handle(Object data) {

                try {

                    JSONObject result = (JSONObject) data;

                    if (result.isNull("errorMsg") != true) {
                        handler.error(result.getString("errorMsg"));
                    } else if (result.isNull("isNotAuthed") != true && result.getBoolean("isNotAuthed") == true) {
                        handler.notAuthed();
                    } else {
                        handler.success(result.getLong("count"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @param handler
     */
    public void count(CountHandler handler) {
        count(null, handler);
    }

    /**
     * @param filter
     * @param handler
     */
    public void exists(JSONObject filter, final ExistsHandler handler) {

        JSONObject params = new JSONObject();

        try {
            if (filter != null) {
                params.put("filter", filter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        room.send("checkExists", params, new MethodHandler() {

            @Override
            public void handle(Object data) {

                try {

                    JSONObject result = (JSONObject) data;

                    if (result.isNull("errorMsg") != true) {
                        handler.error(result.getString("errorMsg"));
                    } else if (result.isNull("isNotAuthed") != true && result.getBoolean("isNotAuthed") == true) {
                        handler.notAuthed();
                    } else {
                        handler.success(result.getBoolean("exists"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @param handler
     */
    public void exists(ExistsHandler handler) {
        exists(null, handler);
    }

    private interface Rooms {
        public void exit();
    }

    /**
     * @param handler
     * @return rooms
     */
    public Rooms onNew(final OnNewHandler handler) {

        final Room roomForCreate = new Room(connector, boxName, name + "/create");

        roomForCreate.on("create", new MethodHandler() {

            @Override
            public void handle(Object savedData) {
                handler.handle((JSONObject) savedData);
            }
        });

        return new Rooms() {

            @Override
            public void exit() {
                roomForCreate.exit();
            }
        };
    }

    /**
     * @param properties
     * @param handler
     * @return rooms
     */
    public Rooms onNew(final JSONObject properties, final OnNewHandler handler) {

        final Iterator<String> keys = properties.keys();

        while (keys.hasNext() == true) {

            String propertyName = keys.next();
            Object value = null;
            try {
                value = properties.get(propertyName);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final Room roomForCreate = new Room(connector, boxName, name + "/" + propertyName + "/" + value + "/create");

            roomForCreate.on("create", new MethodHandler() {

                @Override
                public void handle(Object savedData) {

                    while (keys.hasNext() == true) {

                        String propertyName = keys.next();
                        try {

                            Object value = properties.get(propertyName);

                            if (!((JSONObject) savedData).get(propertyName).equals(value)) {
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    handler.handle((JSONObject) savedData);
                }
            });

            return new Rooms() {

                @Override
                public void exit() {
                    roomForCreate.exit();
                }
            };
        }

        return null;
    }

    /**
     * @param handler
     * @return rooms
     */
    public Rooms onRemove(final OnRemoveHandler handler) {

        final Room roomForRemove = new Room(connector, boxName, name + "/remove");

        roomForRemove.on("remove", new MethodHandler() {

            @Override
            public void handle(Object originData) {
                handler.handle((JSONObject) originData);
            }
        });

        return new Rooms() {

            @Override
            public void exit() {
                roomForRemove.exit();
            }
        };
    }

    /**
     * @param properties
     * @param handler
     * @return rooms
     */
    public Rooms onRemove(final JSONObject properties, final OnRemoveHandler handler) {

        final Iterator<String> keys = properties.keys();

        while (keys.hasNext() == true) {

            String propertyName = keys.next();
            Object value = null;
            try {
                value = properties.get(propertyName);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final Room roomForRemove = new Room(connector, boxName, name + "/" + propertyName + "/" + value + "/remove");

            roomForRemove.on("remove", new MethodHandler() {

                @Override
                public void handle(Object savedData) {

                    while (keys.hasNext() == true) {

                        String propertyName = keys.next();
                        try {

                            Object value = properties.get(propertyName);

                            if (!((JSONObject) savedData).get(propertyName).equals(value)) {
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    handler.handle((JSONObject) savedData);
                }
            });

            return new Rooms() {

                @Override
                public void exit() {
                    roomForRemove.exit();
                }
            };
        }

        return null;
    }
}
