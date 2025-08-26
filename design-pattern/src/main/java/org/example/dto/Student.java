package org.example.dto;

public record Student(String name, Integer age) {

    public static Builder of() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private Integer age;

        public Student build() {
            return new Student(name, age);
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder age(Integer age) {
            this.age = age;
            return this;
        }

    }

}
