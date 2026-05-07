package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.request.CreateCommentRequest;
import com.openclassrooms.mddapi.dto.request.CreatePostRequest;
import com.openclassrooms.mddapi.dto.response.CommentResponse;
import com.openclassrooms.mddapi.dto.response.FeedItemResponse;
import com.openclassrooms.mddapi.dto.response.PostDetailResponse;
import com.openclassrooms.mddapi.dto.response.PostResponse;
import com.openclassrooms.mddapi.entity.Comment;
import com.openclassrooms.mddapi.entity.Post;
import com.openclassrooms.mddapi.entity.Subject;
import com.openclassrooms.mddapi.entity.User;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.mapper.PostMapper;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.SubjectRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final SubjectRepository subjectRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    @Transactional
    public PostResponse createPost(CreatePostRequest request, User author) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", request.getSubjectId()));
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author)
                .subject(subject)
                .build();
        return postMapper.toDto(postRepository.save(post));
    }

    public PostDetailResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", id));
        return postMapper.toDetailDto(post);
    }

    public List<PostResponse> getPostsByUserId(Long userId) {
        return postMapper.toDtoList(postRepository.findByAuthorId(userId));
    }

    @Transactional
    public CommentResponse addComment(Long postId, CreateCommentRequest request, User author) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", postId));
        Comment comment = Comment.builder()
                .content(request.getContent())
                .author(author)
                .post(post)
                .build();
        return postMapper.toCommentDto(commentRepository.save(comment));
    }

    public List<FeedItemResponse> getFeed(Long userId, Sort.Direction direction) {
        User user = userRepository.findByIdWithSubscriptions(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        List<Subject> subscriptions = user.getSubscriptions();
        if (subscriptions.isEmpty()) {
            return List.of();
        }
        Sort sort = Sort.by(direction, "createdAt");
        return postRepository
                .findBySubjectIn(subscriptions, sort)
                .stream()
                .map(postMapper::toFeedItem)
                .collect(java.util.stream.Collectors.toList());
    }
}
