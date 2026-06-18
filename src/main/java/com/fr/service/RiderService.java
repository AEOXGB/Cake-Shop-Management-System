package com.fr.service;

import com.fr.javaBean.Rider;

import java.util.List;

public interface RiderService {

    Rider login(String phone, String password);

    Rider getRiderById(int id);

    boolean addRider(Rider rider);

    boolean updateRider(Rider rider);

    boolean deleteRider(int id);

    List<Rider> getAllRiders();

    Rider getRiderByPhone(String phone);
}