package com.github.courtandrey.simpledatascraperbot.data;

import java.util.Objects;

@SuppressWarnings("unused")
public class Vacancy implements Data {
    private String name;
    private String url;
    private String salary;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return  "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", salary='" + salary + '\'';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vacancy vacancy = (Vacancy) o;
        return Objects.equals(name, vacancy.name) && Objects.equals(url, vacancy.url) && Objects.equals(salary, vacancy.salary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url, salary);
    }
}