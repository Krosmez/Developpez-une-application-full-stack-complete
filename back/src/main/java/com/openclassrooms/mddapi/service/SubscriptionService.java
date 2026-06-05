package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.response.SubjectResponse;
import com.openclassrooms.mddapi.entity.Subject;
import com.openclassrooms.mddapi.entity.User;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.mapper.SubjectMapper;
import com.openclassrooms.mddapi.repository.SubjectRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

  private final UserRepository userRepository;
  private final SubjectRepository subjectRepository;
  private final SubjectMapper subjectMapper;

  public List<SubjectResponse> getSubscriptions(Long userId) {
    User user = userRepository.findByIdWithSubscriptions(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId));
    return subjectMapper.toDtoList(user.getSubscriptions());
  }

  @Transactional
  public List<SubjectResponse> subscribe(Long userId, Long subjectId) {
    User user = userRepository.findByIdWithSubscriptions(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId));
    Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new ResourceNotFoundException("Subject", subjectId));

    boolean alreadySubscribed = user.getSubscriptions().stream().anyMatch(s -> s.getId().equals(subjectId));
    if(alreadySubscribed) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Already subscribed to this subject");
    }

    user.getSubscriptions().add(subject);
    userRepository.save(user);
    return subjectMapper.toDtoList(user.getSubscriptions());
  }

  @Transactional
  public List<SubjectResponse> unsubscribe(Long userId, Long subjectId) {
    User user = userRepository.findByIdWithSubscriptions(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId));
    subjectRepository.findById(subjectId).orElseThrow(() -> new ResourceNotFoundException("Subject", subjectId));

    boolean removed = user.getSubscriptions().removeIf(s -> s.getId().equals(subjectId));
    if(!removed) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not subscribed to this subject");
    }

    userRepository.save(user);
    return subjectMapper.toDtoList(user.getSubscriptions());
  }
}
