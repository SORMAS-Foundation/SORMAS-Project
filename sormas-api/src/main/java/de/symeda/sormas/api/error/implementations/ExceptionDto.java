package de.symeda.sormas.api.error.implementations;

import java.util.List;


public class ExceptionDto {
    int id;
    List<String> argumentsList;

    public int getId() {
        return id;
    }

    public List<String> getArgumentsList() {
        return argumentsList;
    }

    public ExceptionDto(int id, List<String> argumentsList) {
        this.id = id;
        this.argumentsList = argumentsList;
    }
}
