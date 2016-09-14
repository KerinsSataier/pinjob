package ru.pinjob.domain.projections;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface SimplifiedAnnouncement{

    Long getId();

    String getTitle();

    Integer getPrice();

    String getPlace();

    @JsonIgnore
    Class<?> getDecoratedClass();
}
