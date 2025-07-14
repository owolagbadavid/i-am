package me.oreos.iam.services;


import org.springframework.stereotype.Service;
import me.oreos.iam.entities.Group;

@Service
public interface GroupService extends MyBaseService<Group, Integer> {
}
