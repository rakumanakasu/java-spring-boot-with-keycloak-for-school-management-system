package com.dara.su79.services;

import com.dara.su79.exceptions.MyResourceNotFoundException;
import com.dara.su79.models.UserProfile;
import com.dara.su79.repositories.UserProfileRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public List<UserProfile> getAllProfiles() {
        return userProfileRepository.findAll();
    }

    public UserProfile findById(int id) {
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("UserProfile not found with id " + id));
    }

    @Transactional
    public UserProfile createProfile(UserProfile profile) {
        return userProfileRepository.save(profile);
    }

    @Transactional
    public UserProfile updateProfile(int id, UserProfile profile) {
        UserProfile existing = findById(id);
        existing.setPhone(profile.getPhone());
        existing.setAddress(profile.getAddress());
        return userProfileRepository.save(existing);
    }

    @Transactional
    public void deleteProfile(int id) {
        UserProfile existing = findById(id);
        userProfileRepository.delete(existing);
    }
}
