package com.example.football.controller;

import com.example.football.entity.CommunityPost;
import com.example.football.entity.Reply;
import com.example.football.entity.Standing;
import com.example.football.entity.User;
import com.example.football.repository.CommunityRepository;
import com.example.football.repository.ReplyRepository;
import com.example.football.service.CommunityService;
import com.example.football.service.StandingsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class CommunityController {

    private final CommunityRepository communityRepository;
    private final ReplyRepository replyRepository;
    private final StandingsService standingsService;
    private final CommunityService communityService;

    private final String uploadDir = new File("uploads").getAbsolutePath() + File.separator;
    private final String relativePath = "/uploads/";

    @Autowired
    public CommunityController(CommunityRepository communityRepository,
                               ReplyRepository replyRepository,
                               StandingsService standingsService,
                               CommunityService communityService) {
        this.communityRepository = communityRepository;
        this.replyRepository = replyRepository;
        this.standingsService = standingsService;
        this.communityService = communityService;
    }

    /**
     * 커뮤니티 목록 (정렬/페이징 적용)
     */
    @GetMapping("/community")
    public String communityHome(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(value = "sort", required = false, defaultValue = "recent") String sort,
                                Model model) {
        // CommunityService를 통해 정렬 및 페이징된 게시글 가져오기
        Page<CommunityPost> postsPage = communityService.getPosts(page, size, sort);

        // 뷰로 전달할 데이터 추가
        model.addAttribute("posts", postsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postsPage.getTotalPages());
        model.addAttribute("sort", sort);

        // standings 부분
        List<Standing> standings = standingsService.getStandings();
        model.addAttribute("standings", standings);

        return "community"; // community.html 템플릿 반환
    }

    /**
     * 새 글 작성 폼
     */
    @GetMapping("/community/new")
    public String newPostForm(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("author", loggedInUser.getUsername());
        return "newPost";
    }

    /**
     * 새 글 등록 (이미지/첨부파일 업로드 처리)
     */
    @PostMapping("/community")
    public String createPost(@RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam(value = "image", required = false) MultipartFile image,
                             @RequestParam(value = "attachment", required = false) MultipartFile attachment,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        // 로그인된 사용자 체크
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        // 게시글 객체 생성
        CommunityPost post = new CommunityPost();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(loggedInUser.getUsername());

        // 업로드 폴더 생성(없으면) - images, attachments
        ensureUploadDirExists();

        // -----------------------
        // 1) 이미지 업로드 처리
        // -----------------------
        if (image != null && !image.isEmpty()) {
            try {
                String originalFilename = image.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
                String fileName = System.currentTimeMillis() + "_" + originalFilename;

                String absoluteFilePath = uploadDir + "images/" + fileName;
                image.transferTo(new File(absoluteFilePath));

                // DB에 저장할 때는 웹에서 접근 가능한 상대 경로로...
                String imagePath = relativePath + "images/" + fileName;
                post.setImagePath(imagePath);

            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("message", "이미지 업로드 중 오류가 발생했습니다.");
                return "redirect:/community";
            }
        }

        // -----------------------
        // 2) 첨부파일 업로드 처리
        // -----------------------
        if (attachment != null && !attachment.isEmpty()) {
            try {
                String originalFilename = attachment.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
                String fileName = System.currentTimeMillis() + "_" + originalFilename;

                String absoluteFilePath = uploadDir + "attachments/" + fileName;
                attachment.transferTo(new File(absoluteFilePath));

                // DB에는 /uploads/attachments/파일명 식으로 저장
                String attachmentPath = relativePath + "attachments/" + fileName;
                post.setAttachmentPath(attachmentPath);

            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("message", "첨부파일 업로드 중 오류가 발생했습니다.");
                return "redirect:/community";
            }
        }

        // DB 저장
        communityRepository.save(post);
        redirectAttributes.addFlashAttribute("message", "게시글이 작성되었습니다.");
        return "redirect:/community";
    }

    /**
     * 업로드 폴더가 없으면 생성
     */
    private void ensureUploadDirExists() {
        File imageDir = new File(uploadDir + "images/");
        File attachmentDir = new File(uploadDir + "attachments/");

        if (!imageDir.exists() && imageDir.mkdirs()) {
            System.out.println("이미지 업로드 디렉토리가 생성되었습니다: " + imageDir.getAbsolutePath());
        }
        if (!attachmentDir.exists() && attachmentDir.mkdirs()) {
            System.out.println("첨부파일 업로드 디렉토리가 생성되었습니다: " + attachmentDir.getAbsolutePath());
        }
    }

    /**
     * 게시글 상세 보기
     */
    @GetMapping("/community/{id}")
    public String viewPost(@PathVariable("id") Long id,
                           @RequestParam(value = "sort", defaultValue = "latest") String sort,
                           HttpSession session,
                           Model model) {
        CommunityPost post = communityRepository.findById(id).orElse(null);
        if (post != null) {
            post.setViews(post.getViews() + 1);
            communityRepository.save(post);
            model.addAttribute("post", post);

            // 답글 정렬 로직
            List<Reply> replies;
            switch (sort) {
                case "likes":
                    replies = replyRepository.findByPostIdOrderByLikesDesc(id);
                    break;
                case "oldest":
                    replies = replyRepository.findByPostIdOrderByCreatedAtAsc(id);
                    break;
                default: // 최신순 (기본값)
                    replies = replyRepository.findByPostIdOrderByCreatedAtDesc(id);
                    break;
            }
            model.addAttribute("replies", replies);
            model.addAttribute("currentSort", sort); // 현재 정렬 기준 전달
        }

        // 세션에서 로그인된 사용자 ID 가져오기
        Object loggedInUserId = session.getAttribute("loggedInUserId");
        if (loggedInUserId != null) {
            model.addAttribute("userId", loggedInUserId); // 로그인된 사용자 ID 전달
        } else {
            model.addAttribute("userId", null); // 로그인되지 않은 경우 null 전달
        }

        List<Standing> standings = standingsService.getStandings();
        model.addAttribute("standings", standings);

        return "viewPost";
    }

    /**
     * 답글 추가
     */
    @PostMapping("/community/{id}/reply")
    public String addReply(@PathVariable("id") Long id,
                           @RequestParam("content") String content,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        String loggedInUsername = (String) session.getAttribute("loggedInUsername");

        if (loggedInUsername == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        CommunityPost post = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Reply reply = new Reply();
        reply.setPost(post);
        reply.setAuthor(loggedInUsername);
        reply.setContent(content);

        replyRepository.save(reply);

        return "redirect:/community/" + id;
    }

    /**
     * 게시글 수정 폼
     */
    @GetMapping("/community/{id}/edit")
    public String editPostForm(@PathVariable("id") Long id,
                               Model model,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        CommunityPost post = communityRepository.findById(id).orElse(null);
        String loggedInUsername = (String) session.getAttribute("loggedInUsername");

        if (post == null) {
            redirectAttributes.addFlashAttribute("message", "게시글을 찾을 수 없습니다.");
            return "redirect:/community";
        }

        if (!post.getAuthor().equals(loggedInUsername)) {
            redirectAttributes.addFlashAttribute("message", "수정 권한이 없습니다.");
            return "redirect:/community";
        }

        model.addAttribute("post", post);
        return "editPost";
    }

    /**
     * 게시글 수정 처리
     */
    @PostMapping("/community/{id}/update")
    public String updatePost(@PathVariable("id") Long id,
                             @RequestParam("title") String title,
                             @RequestParam("content") String content,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        CommunityPost post = communityRepository.findById(id).orElse(null);
        String loggedInUsername = (String) session.getAttribute("loggedInUsername");

        if (post == null) {
            redirectAttributes.addFlashAttribute("message", "게시글을 찾을 수 없습니다.");
            return "redirect:/community";
        }

        if (!post.getAuthor().equals(loggedInUsername)) {
            redirectAttributes.addFlashAttribute("message", "수정 권한이 없습니다.");
            return "redirect:/community";
        }

        post.setTitle(title);
        post.setContent(content);
        communityRepository.save(post);
        redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 수정되었습니다.");
        return "redirect:/community/" + id;
    }

    /**
     * 게시글 삭제
     */
    @PostMapping("/community/{id}/delete")
    public String deletePost(@PathVariable("id") Long id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        String loggedInUsername = (String) session.getAttribute("loggedInUsername");

        CommunityPost post = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getAuthor().equals(loggedInUsername)) {
            redirectAttributes.addFlashAttribute("message", "삭제 권한이 없습니다.");
            return "redirect:/community";
        }

        communityRepository.delete(post);
        redirectAttributes.addFlashAttribute("message", "게시글이 삭제되었습니다.");
        return "redirect:/community";
    }

    /**
     * 답글 수정 폼
     */
    @GetMapping("/community/{postId}/reply/{replyId}/edit")
    public String editReplyForm(@PathVariable("postId") Long postId,
                                @PathVariable("replyId") Long replyId,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        String loggedInUsername = (String) session.getAttribute("loggedInUsername");

        if (loggedInUsername == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("답글을 찾을 수 없습니다."));

        if (!reply.getAuthor().equals(loggedInUsername)) {
            redirectAttributes.addFlashAttribute("message", "수정 권한이 없습니다.");
            return "redirect:/community/" + postId;
        }

        model.addAttribute("reply", reply);
        model.addAttribute("postId", postId);
        return "editReply";
    }

    /**
     * 답글 수정 처리
     */
    @PostMapping("/community/{postId}/reply/{replyId}/update")
    public String updateReply(@PathVariable("postId") Long postId,
                              @PathVariable("replyId") Long replyId,
                              @RequestParam("content") String content,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        String loggedInUsername = (String) session.getAttribute("loggedInUsername");

        if (loggedInUsername == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("답글을 찾을 수 없습니다."));

        if (!reply.getAuthor().equals(loggedInUsername)) {
            redirectAttributes.addFlashAttribute("message", "수정 권한이 없습니다.");
            return "redirect:/community/" + postId;
        }

        reply.setContent(content);
        replyRepository.save(reply);

        redirectAttributes.addFlashAttribute("message", "답글이 수정되었습니다.");
        return "redirect:/community/" + postId;
    }

    /**
     * 답글 삭제
     */
    @PostMapping("/community/{postId}/reply/{replyId}/delete")
    public String deleteReply(@PathVariable("postId") Long postId,
                              @PathVariable("replyId") Long replyId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        String loggedInUsername = (String) session.getAttribute("loggedInUsername");

        if (loggedInUsername == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("답글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!reply.getAuthor().equals(loggedInUsername)) {
            redirectAttributes.addFlashAttribute("message", "삭제 권한이 없습니다.");
            return "redirect:/community/" + postId;
        }

        replyRepository.delete(reply); // 답글 삭제
        redirectAttributes.addFlashAttribute("message", "답글이 삭제되었습니다.");
        return "redirect:/community/" + postId;
    }

    /**
     * 이미지 파일 서빙
     */
    @GetMapping("/uploads/images/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            String filePath = uploadDir + "images/" + filename;
            Resource file = new UrlResource("file:" + filePath);

            if (file.exists() && file.isReadable()) {
                return ResponseEntity.ok().body(file);
            } else {
                throw new RuntimeException("파일을 찾을 수 없습니다: " + filename);
            }
        } catch (Exception e) {
            throw new RuntimeException("파일 로드 중 오류가 발생: " + filename, e);
        }
    }

    /**
     * 첨부파일 다운로드
     */
    @GetMapping("/uploads/attachments/{filename}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable String filename) {
        try {
            String filePath = uploadDir + "attachments/" + filename;
            Resource resource = new UrlResource("file:" + filePath);

            if (resource.exists() && resource.isReadable()) {
                // OS로부터 MIME 타입 추론
                String mimeType = Files.probeContentType(Paths.get(filePath));
                if (mimeType == null) {
                    // 못 찾으면 기본값 지정
                    mimeType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(mimeType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("첨부파일을 찾을 수 없습니다: " + filename);
            }
        } catch (Exception e) {
            throw new RuntimeException("첨부파일 로드 중 오류가 발생: " + filename, e);
        }
    }

    /**
     * 게시글 좋아요
     */
    @PostMapping("/community/{id}/like")
    @ResponseBody
    public ResponseEntity<String> likePost(@PathVariable("id") Long id) {
        CommunityPost post = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        post.setLikes(post.getLikes() + 1);  // 좋아요 수 증가
        communityRepository.save(post);
        return ResponseEntity.ok("좋아요가 추가되었습니다.");
    }

    /**
     * 게시글 싫어요
     */
    @PostMapping("/community/{id}/dislike")
    @ResponseBody
    public ResponseEntity<String> dislikePost(@PathVariable("id") Long id) {
        CommunityPost post = communityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        post.setDislikes(post.getDislikes() + 1);  // 싫어요 수 증가
        communityRepository.save(post);
        return ResponseEntity.ok("싫어요가 추가되었습니다.");
    }

    // === [답글 좋아요 추가] ===
    /**
     * 답글 좋아요
     */
    @PostMapping("/community/reply/{replyId}/like")
    @ResponseBody
    public ResponseEntity<String> toggleReplyLike(@PathVariable("replyId") Long replyId,
                                                  @RequestParam("userId") Long userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body("로그인 상태가 아닙니다.");
        }

        boolean liked = communityService.toggleReplyLike(replyId, userId);

        if (liked) {
            return ResponseEntity.ok("답글 좋아요가 추가되었습니다.");
        } else {
            return ResponseEntity.ok("답글 좋아요가 취소되었습니다.");
        }
    }

    
}
