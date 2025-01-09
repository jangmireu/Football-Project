package com.example.football.controller;

import com.example.football.entity.CommunityPost;
import com.example.football.entity.Reply;
import com.example.football.entity.Standing;
import com.example.football.entity.User;
import com.example.football.repository.CommunityRepository;
import com.example.football.repository.ReplyRepository;
import com.example.football.service.StandingsService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CommunityController {

	private final CommunityRepository communityRepository;
	private final ReplyRepository replyRepository;
	private final StandingsService standingsService;

	@Autowired
	public CommunityController(CommunityRepository communityRepository, ReplyRepository replyRepository,
			StandingsService standingsService) {
		this.communityRepository = communityRepository;
		this.replyRepository = replyRepository;
		this.standingsService = standingsService;
	}

	// 커뮤니티 홈 화면: 페이지네이션 추가
	@GetMapping("/community")
	public String communityHome(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			Model model) {
		PageRequest pageable = PageRequest.of(page, size);
		Page<CommunityPost> postsPage = communityRepository.findAll(pageable);

		model.addAttribute("posts", postsPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", postsPage.getTotalPages());

		List<Standing> standings = standingsService.getStandings();
		model.addAttribute("standings", standings);

		return "community";
	}

	// 새 글 작성 폼
	@GetMapping("/community/new")
	public String newPostForm(Model model, HttpSession session) {
		User loggedInUser = (User) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			return "redirect:/login";
		}
		model.addAttribute("author", loggedInUser.getUsername());
		return "newPost";
	}

	// 게시글 작성
	@PostMapping("/community")
	public String createPost(@RequestParam("title") String title, @RequestParam("content") String content,
			HttpSession session, RedirectAttributes redirectAttributes) {

		User loggedInUser = (User) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
			return "redirect:/login";
		}

		CommunityPost post = new CommunityPost();
		post.setTitle(title);
		post.setContent(content);
		post.setAuthor(loggedInUser.getUsername());
		communityRepository.save(post);

		redirectAttributes.addFlashAttribute("message", "게시글이 작성되었습니다.");
		return "redirect:/community";
	}

	// 게시글 상세 보기
	@GetMapping("/community/{id}")
	public String viewPost(@PathVariable("id") Long id, Model model) {
		CommunityPost post = communityRepository.findById(id).orElse(null);
		if (post != null) {
			post.setViews(post.getViews() + 1);
			communityRepository.save(post);
			model.addAttribute("post", post);

			List<Reply> replies = replyRepository.findByPostId(id);
			model.addAttribute("replies", replies);
		}

		List<Standing> standings = standingsService.getStandings();
		model.addAttribute("standings", standings);

		return "viewPost";
	}

	// 답글 추가
	@PostMapping("/community/{id}/reply")
	public String addReply(@PathVariable("id") Long id, @RequestParam("content") String content, HttpSession session,
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

	// 게시글 수정 폼
	@GetMapping("/community/{id}/edit")
	public String editPostForm(@PathVariable("id") Long id, Model model, HttpSession session,
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

	// 게시글 수정 처리
	@PostMapping("/community/{id}/update")
	public String updatePost(@PathVariable("id") Long id, @RequestParam("title") String title,
			@RequestParam("content") String content, HttpSession session, RedirectAttributes redirectAttributes) {
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

	// 게시글 삭제
	@PostMapping("/community/{id}/delete")
	public String deletePost(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
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

	// 답글 수정 폼
	@GetMapping("/community/{postId}/reply/{replyId}/edit")
	public String editReplyForm(@PathVariable("postId") Long postId, @PathVariable("replyId") Long replyId,
			HttpSession session, Model model, RedirectAttributes redirectAttributes) {
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

	// 답글 수정 처리
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

	// 답글 삭제
	@PostMapping("/community/{postId}/reply/{replyId}/delete")
	public String deleteReply(@PathVariable("postId") Long postId, @PathVariable("replyId") Long replyId,
			HttpSession session, RedirectAttributes redirectAttributes) {
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

}