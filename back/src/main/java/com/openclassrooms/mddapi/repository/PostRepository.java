package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.entity.Post;
import com.openclassrooms.mddapi.entity.Subject;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

  List<Post> findByAuthorId(Long authorId);

  List<Post> findBySubjectIn(List<Subject> subjects, Sort sort);
}
