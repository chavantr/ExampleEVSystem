package com.mywings.emergencyvehicle.models;

import java.util.List;

public class UserInfoHolder {


    private List<SignalPoints> signalPoints;

    private Hospital hospital;

    public static UserInfoHolder getInstance() {
        return UserInfoHelper.INSTANCE;
    }

    public List<SignalPoints> getSignalPoints() {
        return signalPoints;
    }

    public void setSignalPoints(List<SignalPoints> signalPoints) {
        this.signalPoints = signalPoints;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    private static class UserInfoHelper {
        static final UserInfoHolder INSTANCE = new UserInfoHolder();
    }

}
