package com.johnny;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class json {
    public static void main(String[] args) throws IOException {
//        json.jsonTest();
        json.listTest();
//        json.mapTest();
    }

    public static void jsonTest() {
        ObjectMapper mapper = new ObjectMapper();
        Person person = new Person("johnny", 27);
        String jsonString;
        {
            try {
                jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(person);
                System.out.println(jsonString);
                Person deserializedPerson = mapper.readValue(jsonString, Person.class);
                System.out.println(deserializedPerson);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void listTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<Person> personList = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            Person person = new Person();
            person.setName("Johnny" + i);
            person.setAge(i);
            personList.add(person);
        }
        try {
            String jsonString = mapper.writeValueAsString(personList);
            //System.out.println(jsonString);
            CollectionType javaType = mapper.getTypeFactory()
                    .constructCollectionType(List.class, Person.class);
            List<Person> personList1 = mapper.readValue(jsonString, javaType);
            List<Person> personList2 = mapper.readValue(jsonString, new
                    TypeReference<List<Person>>() {
                    });
            for (Person p : personList1) {
                System.out.println(p.toString());
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static void mapTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map m = new HashMap();
        List<Person> personList = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            Person person = new Person();
            person.setName("Johnny" + i);
            person.setAge(i);
            personList.add(person);
        }
        m.put("personList", personList);
        //m.put("Language", "JAVA");
        //m.put("count", 10);
        try {
            String jsonString = mapper.writeValueAsString(m);
            System.out.println(jsonString);
            //
            MapType javaType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Person.class);
            Map<String, Person> personMap = mapper.readValue(jsonString, javaType);

            //
            Map<String, Person> personMap1 = mapper.readValue(jsonString, new TypeReference<Map<String, Person>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}

class Person {
    private String name;
    private int age;

    public Person() {
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person的toString方法{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}