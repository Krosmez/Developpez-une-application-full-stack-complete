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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.argThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService")
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    private User author;
    private Subject subject;

    @BeforeEach
    void setUp() {
        author = User.builder().id(1L).username("jean").build();
        subject = Subject.builder().id(2L).name("Java").build();
    }

    @Test
    @DisplayName("createPost : crée un article et le mappe")
    void createPost_success() {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Titre");
        request.setContent("Contenu");
        request.setSubjectId(2L);

        Post saved = Post.builder().id(10L).title("Titre").content("Contenu").author(author).subject(subject).build();
        PostResponse dto = new PostResponse(10L, "Titre", "Contenu");

        when(subjectRepository.findById(2L)).thenReturn(Optional.of(subject));
        when(postRepository.save(any(Post.class))).thenReturn(saved);
        when(postMapper.toDto(saved)).thenReturn(dto);

        PostResponse result = postService.createPost(request, author);

        assertThat(result).isEqualTo(dto);
        verify(postRepository).save(argThat(p ->
                p.getTitle().equals("Titre")
                        && p.getContent().equals("Contenu")
                        && p.getAuthor() == author
                        && p.getSubject() == subject));
    }

    @Test
    @DisplayName("createPost : lève ResourceNotFound si le sujet est absent")
    void createPost_subjectNotFound() {
        CreatePostRequest request = new CreatePostRequest();
        request.setSubjectId(99L);
        when(subjectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.createPost(request, author))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("getPostById : retourne le détail")
    void getPostById_found() {
        Post post = Post.builder().id(10L).build();
        PostDetailResponse dto = new PostDetailResponse();
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postMapper.toDetailDto(post)).thenReturn(dto);

        assertThat(postService.getPostById(10L)).isEqualTo(dto);
    }

    @Test
    @DisplayName("getPostById : lève ResourceNotFound si absent")
    void getPostById_notFound() {
        when(postRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPostById(10L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("getPostsByUserId : délègue au repository et au mapper")
    void getPostsByUserId() {
        List<Post> posts = List.of(Post.builder().id(10L).build());
        List<PostResponse> dtos = List.of(new PostResponse(10L, "t", "c"));
        when(postRepository.findByAuthorId(1L)).thenReturn(posts);
        when(postMapper.toDtoList(posts)).thenReturn(dtos);

        assertThat(postService.getPostsByUserId(1L)).isEqualTo(dtos);
    }

    @Test
    @DisplayName("addComment : ajoute un commentaire")
    void addComment_success() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("Bien !");
        Post post = Post.builder().id(10L).build();
        Comment saved = Comment.builder().id(5L).content("Bien !").author(author).post(post).build();
        CommentResponse dto = new CommentResponse(5L, "Bien !", null);

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);
        when(postMapper.toCommentDto(saved)).thenReturn(dto);

        CommentResponse result = postService.addComment(10L, request, author);

        assertThat(result).isEqualTo(dto);
        verify(commentRepository).save(argThat(c ->
                c.getContent().equals("Bien !")
                        && c.getAuthor() == author
                        && c.getPost() == post));
    }

    @Test
    @DisplayName("addComment : lève ResourceNotFound si l'article est absent")
    void addComment_postNotFound() {
        CreateCommentRequest request = new CreateCommentRequest();
        when(postRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.addComment(10L, request, author))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("getFeed : retourne les articles des sujets abonnés")
    void getFeed_withSubscriptions() {
        author.setSubscriptions(List.of(subject));
        Post post = Post.builder().id(10L).subject(subject).build();
        FeedItemResponse feedItem = new FeedItemResponse();

        when(userRepository.findByIdWithSubscriptions(1L)).thenReturn(Optional.of(author));
        when(postRepository.findBySubjectIn(eq(List.of(subject)), any(Sort.class))).thenReturn(List.of(post));
        when(postMapper.toFeedItem(post)).thenReturn(feedItem);

        List<FeedItemResponse> result = postService.getFeed(1L, Sort.Direction.DESC);

        assertThat(result).containsExactly(feedItem);
    }

    @Test
    @DisplayName("getFeed : retourne une liste vide sans abonnement")
    void getFeed_noSubscriptions() {
        author.setSubscriptions(List.of());
        when(userRepository.findByIdWithSubscriptions(1L)).thenReturn(Optional.of(author));

        assertThat(postService.getFeed(1L, Sort.Direction.ASC)).isEmpty();
        verify(postRepository, never()).findBySubjectIn(any(), any());
    }

    @Test
    @DisplayName("getFeed : lève ResourceNotFound si l'utilisateur est absent")
    void getFeed_userNotFound() {
        when(userRepository.findByIdWithSubscriptions(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getFeed(1L, Sort.Direction.DESC))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
