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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<FeedItemResponse> getFeed(User currentUser, Pageable pageable) {
        List<Subject> subscriptions = currentUser.getSubscriptions();
        if (subscriptions.isEmpty()) {
            return Page.empty(pageable);
        }
        return postRepository
                .findBySubjectInOrderByCreatedAtDesc(subscriptions, pageable)
                .map(postMapper::toFeedItem);
    }
}
