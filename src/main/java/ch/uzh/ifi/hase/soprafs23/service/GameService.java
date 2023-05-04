package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.game.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.game.Category;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
import ch.uzh.ifi.hase.soprafs23.repository.AnswerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.RoundRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.LeaderboardGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.ScoreboardGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.game.WinnerGetDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.DTO.LetterDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.DTO.GameUsersDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final RoundRepository roundRepository;
    private final RoundService roundService;

    private final AnswerRepository answerRepository;

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository,
                       @Qualifier("userRepository") UserRepository userRepository,
                       @Qualifier("roundRepository") RoundRepository roundRepository,
                       @Qualifier("roundService") RoundService roundService,
                       @Qualifier("answerRepository") AnswerRepository answerRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.roundRepository = roundRepository;
        this.roundService = roundService;
        this.answerRepository = answerRepository;
    }

    public int createGame(Game newGame, String userToken) {

        User user = getUserByToken(userToken);

        checkIfUserExists(user);

        checkIfHostIsEligible(user.getId());

        newGame.setGamePin(generateGamePin());
        newGame.setStatus(GameStatus.OPEN);
        newGame.setHostId(user.getId());
        newGame.addPlayer(user);
        newGame.setRoundLetters(generateRandomLetters(newGame.getRounds()));
        newGame.setCurrentRound(1);

        newGame = gameRepository.save(newGame);
        gameRepository.flush();

        roundService.createAllRounds(newGame);

        log.debug("Created following game: {}", newGame);
        return newGame.getGamePin();
    }

    public List<Character> generateRandomLetters(Long numberOfRounds){
        List<Character> letters = new ArrayList<>();

        for (char letter = 'A'; letter <= 'Z'; letter++) {
            letters.add(letter);
        }

        Collections.shuffle(letters);

        return letters.subList(0, numberOfRounds.intValue());
    }

    public GameUsersDTO joinGame(int gamePin, String userToken) {

        User user = getUserByToken(userToken);

        checkIfUserExists(user);

        checkIfUserCanJoin(user.getId());

        Game gameToJoin = gameRepository.findByGamePin(gamePin);

        checkIfGameExists(gameToJoin);

        gameToJoin.addPlayer(user);

        return getHostAndAllUserNamesOfGame(gameToJoin);
    }

    public GameUsersDTO leaveGame(int gamePin, String userToken) {

        User user = getUserByToken(userToken);

        checkIfUserExists(user);

        Game game = gameRepository.findByGamePin(gamePin);

        checkIfGameExists(game);

        checkIfUserInGame(user, game);

        Boolean userIsHost = checkIfUserIsHost(user, game);

        game.removePlayer(user);

        Boolean gameHasUsers = checkIfGameHasUsers(game);

        if (userIsHost && gameHasUsers) {
            setNewHost(game);
        } else if (!gameHasUsers) {
            deleteGameAndRounds(game);
            return new GameUsersDTO();
        }

        return getHostAndAllUserNamesOfGame(game);
    }

    public Game getGameByGamePin(int gamePin) {

        Game game = gameRepository.findByGamePin(gamePin);

        String errorMessage = "Game does not exist!";

        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format(errorMessage));
        }
        return game;
    }


    public List<String> getGameCategoryNames(Game game) {
        List<Category> gameCategories = game.getCategories();

        List<String> gameCategoryNames = new ArrayList<>();

        for (Category gameCategory : gameCategories) {
            gameCategoryNames.add(gameCategory.getName());
        }
        return gameCategoryNames;
    }

    public LetterDTO startGame(int gamePin){

        Game game = gameRepository.findByGamePin(gamePin);
        checkIfGameExists(game);

        game.setStatus(GameStatus.RUNNING);

        return roundService.nextRound(gamePin);

    }

    /**
     * Helper methods to aid in the game creation, modification and deletion
     */

    private List<Game> getOpenGames() {
        return gameRepository.findByStatus(GameStatus.OPEN);
    }

    private List<Game> getRunningGames() {
        return gameRepository.findByStatus(GameStatus.RUNNING);
    }

    private List<Game> getOpenOrRunningGames() {
        List<Game> openOrRunningGames = getOpenGames();
        openOrRunningGames.addAll(getRunningGames());
        return openOrRunningGames;
    }

    private List<Long> getGameUsersId(Game game) {
        List<Long> usersId = new ArrayList<>();
        for (User user : game.getUsers()) {
            usersId.add(user.getId());
        }
        return usersId;
    }

    private void checkIfHostIsEligible(Long hostId) {
        List<Game> openOrRunningGames = getOpenOrRunningGames();

        String errorMessage = "You are already part of a game." +
                "You cannot host another game!";
        for (Game game : openOrRunningGames) {
            List<Long> userIds = getGameUsersId(game);
            if (userIds.contains(hostId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        String.format(errorMessage));
            }
        }
    }

    void checkIfUserCanJoin(Long userId) {

        List<Game> openOrRunningGames = getOpenOrRunningGames();

        String errorMessage = "You are already part of a game." +
                "You cannot join another game!";

        for (Game game : openOrRunningGames) {
            List<Long> userIds = getGameUsersId(game);
            if (userIds.contains(userId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        String.format(errorMessage));
            }
        }
    }

    void checkIfUserExists(User user) {

        String errorMessage = "User does not exist." +
                "Please register before playing!";
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }

    private void checkIfUserInGame(User user, Game game) {
        List<User> gameUsers = game.getUsers();

        String errorMessage = "You are not part of this game or the game is already running.";

        if (!gameUsers.contains(user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }

    private Boolean checkIfUserIsHost(User user, Game game) {
        Long hostId = game.getHostId();

        String errorMessage = "You are not part of this game or the game is already running.";

        return hostId.equals(user.getId());
    }

    private Boolean checkIfGameHasUsers(Game game) {
        List<User> users = game.getUsers();
        return users.size() > 0;
    }

    private void deleteGameAndRounds(Game game) {
        List<Round> rounds = roundRepository.findByGame(game);
        for (Round round : rounds) {
            roundRepository.delete(round);
        }
        gameRepository.delete(game);
    }

    private void setNewHost(Game game) {
        List<User> users = game.getUsers();
        Random rand = new Random();
        User hostCandidate = users.get(rand.nextInt(users.size()));
        game.setHostId(hostCandidate.getId());
    }

    public void checkIfGameExists(Game game) {

        String errorMessage = "Game does not exist or is not open anymore." +
                "Please try again with a different pin!";
        if (game == null || !game.getStatus().equals(GameStatus.OPEN)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(errorMessage));
        }
    }

    public GameUsersDTO getHostAndAllUserNamesOfGame(Game gameToJoin) {
        User host = userRepository.findById(gameToJoin.getHostId()).orElse(null);
        List<User> users = gameToJoin.getUsers();
        List<String> usernames = new ArrayList<>();
        for (User user : users) {
            if (!user.equals(host)) {
                usernames.add(user.getUsername());
            }
        }
        GameUsersDTO gameUsersDTO = new GameUsersDTO();
        gameUsersDTO.setHostUsername(host.getUsername());
        gameUsersDTO.setUsernames(usernames);
        return gameUsersDTO;
    }

    private User getUserByToken(String userToken) {
        return userRepository.findByToken(userToken);
    }

    private int generateGamePin() {

        Random rnd = new Random();

        return rnd.nextInt(9000) + 1000;
    }


    Map<User, Integer> calculateUserScores(int gamePin) {
        List<Answer> answers = answerRepository.findAllByGamePin(gamePin);

        Map<User, Integer> userScores = new HashMap<>();
        for (Answer answer : answers) {
            User user = answer.getUser();
            int currentScore = userScores.getOrDefault(user, 0);
            currentScore += answer.getScorePoint().getPoints();
            userScores.put(user, currentScore);
        }

        return userScores;
    }

    public List<WinnerGetDTO> getWinner(int gamePin) {
        Map<User, Integer> userScores = calculateUserScores(gamePin);

        List<WinnerGetDTO> winners = new ArrayList<>();
        int maxScore = -1;
        for (Map.Entry<User, Integer> entry : userScores.entrySet()) {
            if (entry.getValue() > maxScore) {
                winners.clear();
                WinnerGetDTO winnerDTO = new WinnerGetDTO();
                winnerDTO.setUser(entry.getKey());
                winnerDTO.setScore(entry.getValue().longValue());
                winners.add(winnerDTO);
                maxScore = entry.getValue();
            } else if (entry.getValue() == maxScore) {
                WinnerGetDTO winnerDTO = new WinnerGetDTO();
                winnerDTO.setUser(entry.getKey());
                winnerDTO.setScore(entry.getValue().longValue());
                winners.add(winnerDTO);
            }
        }

        return winners;
    }

    public List<ScoreboardGetDTO> getScoreboard(int gameId) {
        Map<User, Integer> userScores = calculateUserScores(gameId);

        List<ScoreboardGetDTO> scoreboard = new ArrayList<>();
        for (Map.Entry<User, Integer> entry : userScores.entrySet()) {
            ScoreboardGetDTO scoreboardEntry = new ScoreboardGetDTO();
            scoreboardEntry.setUser(entry.getKey());
            scoreboardEntry.setScore(entry.getValue());
            scoreboard.add(scoreboardEntry);
        }

        return scoreboard;
    }

    public List<LeaderboardGetDTO> getLeaderboard() {

        List<User> users = userRepository.findAll();

        Map<User, Integer> leaderboard = new HashMap<>();

        // Initialize all users with a score of 0
        for (User user : users) {
            leaderboard.put(user, 0);
        }

        for (User user: users){
            int totalUserScore = 0;
            List<Answer> userAnswer = answerRepository.findAllByUser(user);
            for (Answer answer: userAnswer){
                totalUserScore += answer.getScorePoint().getPoints();
            }
            leaderboard.put(user, totalUserScore);
        }

        // Sort the leaderboard map by scores in descending order and create LeaderboardGetDTO objects

        return leaderboard.entrySet().stream()
                .sorted(Map.Entry.<User, Integer>comparingByValue().reversed())
                .map(entry -> {
                    LeaderboardGetDTO leaderboardGetDTO = new LeaderboardGetDTO();
                    leaderboardGetDTO.setUser(entry.getKey());
                    leaderboardGetDTO.setAccumulatedScore(entry.getValue());
                    return leaderboardGetDTO;
                })
                .collect(Collectors.toList());
    }
}
