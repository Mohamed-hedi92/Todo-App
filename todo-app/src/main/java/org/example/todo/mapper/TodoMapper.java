package org.example.todo.mapper;

public class TodoMapper {

    public static org.openapitools.model.Todo toApiTodo(org.example.todo.model.Todo entity) {
        if (entity == null) return null;
        org.openapitools.model.Todo apiTodo = new org.openapitools.model.Todo();
        apiTodo.setId(entity.getId() != null ? entity.getId().intValue() : null);
        apiTodo.setTitle(entity.getTitle());
        apiTodo.setDone(entity.isDone());
        return apiTodo;
    }

    public static org.example.todo.model.Todo toEntityTodo(org.openapitools.model.Todo apiTodo) {
        if (apiTodo == null) return null;
        org.example.todo.model.Todo entity = new org.example.todo.model.Todo();
        if (apiTodo.getId() != null) {
            entity.setId(apiTodo.getId().longValue());
        }
        entity.setTitle(apiTodo.getTitle());
        entity.setDone(apiTodo.getDone() != null ? apiTodo.getDone() : false);
        return entity;
    }
}
