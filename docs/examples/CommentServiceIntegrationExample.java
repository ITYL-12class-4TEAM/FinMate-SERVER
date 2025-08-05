// 댓글 서비스 연동 예시 - CommentServiceImpl.java에 추가할 코드

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final NotificationHelper notificationHelper; // 추가

    @Override
    public CommentResponseDTO createComment(CommentCreateRequestDTO request, Long memberId) {
        // 기존 댓글 생성 로직
        CommentVO comment = CommentVO.builder()
                .postId(request.getPostId())
                .content(request.getContent())
                .authorId(memberId)
                .createdAt(LocalDateTime.now())
                .build();

        commentMapper.insertComment(comment);

        // 알림 생성 (추가된 부분)
        try {
            // 게시글 정보 조회
            PostVO post = postMapper.selectPostById(request.getPostId());
            MemberVO author = memberMapper.selectById(memberId);

            if (post != null && author != null) {
                notificationHelper.notifyCommentCreated(
                    request.getPostId(),
                    comment.getId(),
                    memberId,
                    author.getNickname(),
                    post.getTitle()
                );
            }
        } catch (Exception e) {
            log.warn("댓글 알림 생성 실패 (댓글 생성은 성공): commentId={}", comment.getId(), e);
        }

        return convertToResponseDTO(comment);
    }
}
