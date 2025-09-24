package com.willngo.image_gallery.datastore;

import com.willngo.image_gallery.profile.UserProfile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Repository
public class FakeUserProfileDataStore {

    private static final List<UserProfile> USER_PROFILES = new ArrayList<>();

    static {
        USER_PROFILES.add(new UserProfile(UUID.fromString("d17e2e34-3b79-4619-bf25-5d31f1c2afae"), "janetjones", null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("3aae19c2-9847-442a-9a60-2220dd92ab05"), "antoniojunior", null));
    }

    public List<UserProfile> getUserProfiles(){
        return USER_PROFILES;
    }
}
