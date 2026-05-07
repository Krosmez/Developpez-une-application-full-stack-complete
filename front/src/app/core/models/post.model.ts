import { Author } from './user.model';
import { Subject } from './subject.model';

export interface Comment {
  id: number;
  content: string;
  author: Author;
}

export interface PostDetail {
  id: number;
  title: string;
  content: string;
  author: Author;
  comments: Comment[];
}

export interface PostSummary {
  id: number;
  title: string;
}

export interface FeedItem {
  post: PostSummary;
  subject: Subject;
}

export interface CreatePostRequest {
  title: string;
  content: string;
  subjectId: number;
}

export interface CreateCommentRequest {
  content: string;
}
