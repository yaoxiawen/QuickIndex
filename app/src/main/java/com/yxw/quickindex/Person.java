package com.yxw.quickindex;

import android.support.annotation.NonNull;

public class Person implements Comparable {
    private String name;
    private String pinyin;

    public Person(String name) {
        super();
        this.name = name;
        this.pinyin = PinyinUtils.getPinyin(name);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPinyin() {
        return pinyin;
    }
    @Override
    public int compareTo(@NonNull Object o) {
        Person another = (Person) o;
        return this.pinyin.compareTo(another.getPinyin());
    }
}
