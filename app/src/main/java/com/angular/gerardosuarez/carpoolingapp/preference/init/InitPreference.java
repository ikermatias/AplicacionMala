package com.angular.gerardosuarez.carpoolingapp.preference.init;

public interface InitPreference {

    boolean wasVideoShown();

    void putWasVideoShown(boolean value);

    boolean wasTermsAndConditionsAccepted();

    void putWasTermsAndCondictionsAccepted(boolean value);

    boolean isAlreadyRegistered();

    void putAlreadyRegistered(boolean value);

    boolean wasComunityChoosed();

    void putCommunityChoosed(boolean value);

}
