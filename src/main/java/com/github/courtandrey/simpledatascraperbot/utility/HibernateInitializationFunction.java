package com.github.courtandrey.simpledatascraperbot.utility;

import com.github.courtandrey.simpledatascraperbot.entity.request.HibernateInitIgnore;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.SneakyThrows;
import org.hibernate.Hibernate;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;

public class HibernateInitializationFunction implements Consumer<Object> {

    @Override
    public void accept(Object o) {
        init(o);
    }

    private void init(Object object) {
        Class<?> reqClass = object.getClass();
        Arrays.stream(reqClass.getDeclaredFields())
                .filter(this::lazyCandidate).forEach(field -> tryInit(object, field));
    }

    private boolean lazyCandidate(Field field) {
        return (field.isAnnotationPresent(ElementCollection.class) || field.isAnnotationPresent(OneToMany.class)
                || field.isAnnotationPresent(ManyToMany.class) || field.isAnnotationPresent(OneToOne.class)) &&
                !field.isAnnotationPresent(HibernateInitIgnore.class);
    }

    @SneakyThrows
    private void tryInit(Object o, Field field) {
        boolean accessible = field.canAccess(o);
        field.setAccessible(true);
        if (!Hibernate.isInitialized(field.get(o))) {
            Hibernate.initialize(field.get(o));
        }
        field.setAccessible(accessible);
    }
}
