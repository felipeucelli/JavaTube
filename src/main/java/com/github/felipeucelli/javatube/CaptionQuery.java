package com.github.felipeucelli.javatube;

import java.util.*;

public class CaptionQuery {
    Map<String, Captions> langCodeIndex = new HashMap<>();

    @Override
    public String toString(){
        return langCodeIndex.toString();
    }

    public CaptionQuery(ArrayList<Captions> captions){
        for(Captions code : captions){
            langCodeIndex.put(code.getCode(), code);
        }
    }
    public Captions getByCode(String code){
        return langCodeIndex.get(code);
    }
}