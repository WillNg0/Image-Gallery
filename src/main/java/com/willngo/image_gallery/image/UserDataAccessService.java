package com.willngo.image_gallery.image;

import com.willngo.image_gallery.datastore.FakeUserProfileDataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDataAccessService {

    /*
    can use dependency injection to change "FakeUserProfileDataStore" e.g. postgres, mysql
    how to implement into an interface
    how to switch implementation
     */
    private final FakeUserProfileDataStore fakeUserProfileDataStore;

    @Autowired
    public UserDataAccessService(FakeUserProfileDataStore fakeUserProfileDataStore) {
        this.fakeUserProfileDataStore = fakeUserProfileDataStore;
    }

    List<Image> getUserProfiles() {
        return fakeUserProfileDataStore.getUserProfiles();
    }
}
