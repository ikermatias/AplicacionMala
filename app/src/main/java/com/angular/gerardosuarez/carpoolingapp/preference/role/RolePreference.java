package com.angular.gerardosuarez.carpoolingapp.preference.role;

public interface RolePreference {
    public final static String ROLE_DRIVER = "driver";
    public final static String ROLE_PASSENGER = "passenger";

    void putCurrentRole(String value);

    String getCurrentRole();

    void putCurrentTag(String value);

    String getCurrentTag();
}
