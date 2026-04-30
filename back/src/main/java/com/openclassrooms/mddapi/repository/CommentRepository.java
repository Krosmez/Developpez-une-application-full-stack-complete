package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
