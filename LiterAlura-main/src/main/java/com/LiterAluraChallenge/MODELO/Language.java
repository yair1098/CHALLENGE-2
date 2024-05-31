package com.LiterAluraChallenge.MODELO;

public enum Language {
    ES("es"),
    EN("en"),
    FR("fr"),
    PT("pt");

    private String lang;

    Language(String lang) {
        this.lang = lang;
    }
    public static Language fromString(String text){
        for (Language lenguaje : Language.values()){
            if(lenguaje.lang.equalsIgnoreCase(text)){
                return lenguaje;
            }
        }
        throw new IllegalArgumentException("No se ha encontrado un lenguaje: " + text);
    }

    public String getLang(){
        return this.lang;
    }
}
