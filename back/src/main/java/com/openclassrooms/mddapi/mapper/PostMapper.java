package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.response.AuthorDto;
import com.openclassrooms.mddapi.dto.response.CommentResponse;
import com.openclassrooms.mddapi.dto.response.FeedItemResponse;
import com.openclassrooms.mddapi.dto.response.PostDetailResponse;
import com.openclassrooms.mddapi.dto.response.PostResponse;
import com.openclassrooms.mddapi.dto.response.PostSummary;
import com.openclassrooms.mddapi.dto.response.SubjectResponse;
import com.openclassrooms.mddapi.entity.Comment;
import com.openclassrooms.mddapi.entity.Post;
import com.openclassrooms.mddapi.entity.Subject;
import com.openclassrooms.mddapi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostResponse toDto(Post post);

    List<PostResponse> toDtoList(List<Post> posts);

    @Mapping(target = "author", source = "author")
    @Mapping(target = "comments", source = "comments")
    PostDetailResponse toDetailDto(Post post);

    @Mapping(target = "author", source = "author")
    CommentResponse toCommentDto(Comment comment);

    @Mapping(target = "post.id", source = "id")
    @Mapping(target = "post.title", source = "title")
    @Mapping(target = "subject", source = "subject")
    FeedItemResponse toFeedItem(Post post);

    AuthorDto toAuthorDto(User user);

    SubjectResponse toSubjectDto(Subject subject);

    PostSummary toPostSummary(Post post);
}
