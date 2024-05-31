package com.LiterAluraChallenge.SERVICIOS;

public interface IDataConverter {
    <T> T getData(String json, Class<T> tClass);
}
