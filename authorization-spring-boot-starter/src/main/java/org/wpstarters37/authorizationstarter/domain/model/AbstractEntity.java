package org.wpstarters37.authorizationstarter.domain.model;

public interface AbstractEntity<E> {
    E getId();
    void setId(E id);
}

