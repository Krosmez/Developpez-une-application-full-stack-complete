package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.response.CommentResponse;
import com.openclassrooms.mddapi.dto.response.FeedItemResponse;
import com.openclassrooms.mddapi.dto.response.PostDetailResponse;
import com.openclassrooms.mddapi.dto.response.PostResponse;
import com.openclassrooms.mddapi.entity.Comment;
import com.openclassrooms.mddapi.entity.Post;
import com.openclassrooms.mddapi.entity.Subject;
import com.openclassrooms.mddapi.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PostMapper")
class PostMapperTest {

    private final PostMapper mapper = Mappers.getMapper(PostMapper.class);

    private User author;
    private Subject subject;
    private Post post;

    @BeforeEach
    void setUp() {
        author = User.builder().id(1L).username("jean").build();
        subject = Subject.builder().id(2L).name("Java").description("desc").build();
        Comment comment = Comment.builder().id(3L).content("Bien !").author(author).build();
        post = Post.builder()
                .id(10L)
                .title("Titre")
                .content("Contenu")
                .author(author)
                .subject(subject)
                .comments(List.of(comment))
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0))
                .build();
    }

    @Test
    @DisplayName("toDto : mappe les champs résumés")
    void toDto() {
        PostResponse dto = mapper.toDto(post);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getTitle()).isEqualTo("Titre");
        assertThat(dto.getContent()).isEqualTo("Contenu");
    }

    @Test
    @DisplayName("toDtoList : mappe une liste")
    void toDtoList() {
        List<PostResponse> dtos = mapper.toDtoList(List.of(post));

        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("toDetailDto : mappe auteur, sujet, commentaires et date")
    void toDetailDto() {
        PostDetailResponse dto = mapper.toDetailDto(post);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getAuthor().getId()).isEqualTo(1L);
        assertThat(dto.getAuthor().getUsername()).isEqualTo("jean");
        assertThat(dto.getSubject().getName()).isEqualTo("Java");
        assertThat(dto.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 1, 1, 12, 0));
        assertThat(dto.getComments()).hasSize(1);
        assertThat(dto.getComments().get(0).getContent()).isEqualTo("Bien !");
    }

    @Test
    @DisplayName("toCommentDto : mappe le commentaire et son auteur")
    void toCommentDto() {
        Comment comment = Comment.builder().id(3L).content("Bien !").author(author).build();

        CommentResponse dto = mapper.toCommentDto(comment);

        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getContent()).isEqualTo("Bien !");
        assertThat(dto.getAuthor().getUsername()).isEqualTo("jean");
    }

    @Test
    @DisplayName("toFeedItem : mappe le post imbriqué et le sujet")
    void toFeedItem() {
        FeedItemResponse dto = mapper.toFeedItem(post);

        assertThat(dto.getPost().getId()).isEqualTo(10L);
        assertThat(dto.getPost().getTitle()).isEqualTo("Titre");
        assertThat(dto.getPost().getAuthor().getUsername()).isEqualTo("jean");
        assertThat(dto.getSubject().getName()).isEqualTo("Java");
    }

    @Test
    @DisplayName("null en entrée -> null en sortie")
    void nullSafety() {
        assertThat(mapper.toDto(null)).isNull();
        assertThat(mapper.toAuthorDto(null)).isNull();
        assertThat(mapper.toSubjectDto(null)).isNull();
        assertThat(mapper.toPostSummary(null)).isNull();
    }
}
