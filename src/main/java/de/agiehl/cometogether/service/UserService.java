package de.agiehl.cometogether.service;

import de.agiehl.cometogether.domain.model.User;
import de.agiehl.cometogether.domain.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WordlistService wordlistService;

    public UserService(UserRepository userRepository, WordlistService wordlistService) {
        this.userRepository = userRepository;
        this.wordlistService = wordlistService;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByAccessCodeword(String accessCodeword) {
        return userRepository.findByAccessCodewordIgnoreCase(accessCodeword);
    }

    public User createUser(String name, String accessCodeword) {
        userRepository.findByAccessCodewordIgnoreCase(accessCodeword).ifPresent(u -> {
            throw new DataIntegrityViolationException("Zugangscodewort ist bereits vergeben.");
        });
        User user = new User();
        user.setName(name);
        user.setAccessCodeword(accessCodeword);
        return userRepository.save(user);
    }

    public User updateUser(Long id, String name, String accessCodeword) {
        userRepository.findByAccessCodewordIgnoreCase(accessCodeword).ifPresent(u -> {
            if (!u.getId().equals(id)) {
                throw new DataIntegrityViolationException("Zugangscodewort ist bereits vergeben.");
            }
        });
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        user.setName(name);
        user.setAccessCodeword(accessCodeword);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Map<String, List<String>> suggestAccessCodewords() {
        Set<String> usedCodewords = userRepository.findAll().stream()
                .map(user -> user.getAccessCodeword().toLowerCase())
                .collect(Collectors.toSet());

        Map<String, List<String>> suggestions = new LinkedHashMap<>();
        suggestions.put("Deutsche Wörter", getSuggestions(wordlistService.getGermanWords(), usedCodewords, 5));
        suggestions.put("Brettspiele", getSuggestions(wordlistService.getGermanBoardGames(), usedCodewords, 5));
        suggestions.put("Videospiele", getSuggestions(wordlistService.getGermanVideoGames(), usedCodewords, 5));
        suggestions.put("RPG & Fantasy", getSuggestions(wordlistService.getRpgAndFantasy(), usedCodewords, 5));
        suggestions.put("Nerd-Begriffe", getSuggestions(wordlistService.getNerdStuff(), usedCodewords, 5));
        suggestions.put("TV, Filme & Serien", getSuggestions(wordlistService.getTvMovies(), usedCodewords, 5));

        return suggestions;
    }

    private List<String> getSuggestions(List<String> wordlist, Set<String> usedCodewords, int count) {
        List<String> availableWords = new ArrayList<>(wordlist);
        Collections.shuffle(availableWords);
        return availableWords.stream()
                .filter(word -> !usedCodewords.contains(word.toLowerCase()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
