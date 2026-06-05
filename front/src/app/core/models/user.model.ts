export interface Author {
  id: number;
  username: string;
}

export interface UserProfile {
  id: number;
  email: string;
  username: string;
  bio: string;
}

export interface UpdateUserRequest {
  email?: string;
  username?: string;
  password?: string;
  bio?: string;
}
