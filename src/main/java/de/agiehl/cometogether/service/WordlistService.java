package de.agiehl.cometogether.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WordlistService {

    private final List<String> germanWords;
    private final List<String> germanBoardGames;
    private final List<String> germanVideoGames;
    private final List<String> rpgAndFantasy;
    private final List<String> nerdStuff;
    private final List<String> tvMovies;

    public WordlistService() throws IOException {
        germanWords = loadWords("classpath:wordlists/german_words.txt");
        germanBoardGames = loadWords("classpath:wordlists/german_board_games.txt");
        germanVideoGames = loadWords("classpath:wordlists/german_video_games.txt");
        rpgAndFantasy = loadWords("classpath:wordlists/rpg_and_fantasy.txt");
        nerdStuff = loadWords("classpath:wordlists/nerd_stuff.txt");
        tvMovies = loadWords("classpath:wordlists/tv_movies.txt");
    }

    private List<String> loadWords(String path) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource(path);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    public List<String> getGermanWords() {
        return germanWords;
    }

    public List<String> getGermanBoardGames() {
        return germanBoardGames;
    }

    public List<String> getGermanVideoGames() {
        return germanVideoGames;
    }

    public List<String> getRpgAndFantasy() {
        return rpgAndFantasy;
    }

    public List<String> getNerdStuff() {
        return nerdStuff;
    }

    public List<String> getTvMovies() {
        return tvMovies;
    }
}
