        package ch.uzh.ifi.hase.soprafs23.service;

        import ch.uzh.ifi.hase.soprafs23.constant.Constant;
        import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
        import ch.uzh.ifi.hase.soprafs23.constant.RoundStatus;
        import ch.uzh.ifi.hase.soprafs23.entity.User;
        import ch.uzh.ifi.hase.soprafs23.entity.game.Game;
        import ch.uzh.ifi.hase.soprafs23.entity.game.Round;
        import ch.uzh.ifi.hase.soprafs23.entity.game.SkipManager;
        import ch.uzh.ifi.hase.soprafs23.entity.quote.FactHolder;
        import ch.uzh.ifi.hase.soprafs23.helper.RoundHelper;
        import ch.uzh.ifi.hase.soprafs23.helper.WebSocketDTOCreator;
        import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
        import ch.uzh.ifi.hase.soprafs23.repository.RoundRepository;
        import ch.uzh.ifi.hase.soprafs23.repository.SkipRepository;
        import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
        import ch.uzh.ifi.hase.soprafs23.websocketDto.*;
        import ch.uzh.ifi.hase.soprafs23.websocketDto.TimerDto.ResultTimerDTO;
        import ch.uzh.ifi.hase.soprafs23.websocketDto.TimerDto.ScoreboardTimerDTO;
        import ch.uzh.ifi.hase.soprafs23.websocketDto.TimerDto.VotingTimerDTO;
        import ch.uzh.ifi.hase.soprafs23.websocketDto.votingDto.ResultNextVoteDTO;
        import ch.uzh.ifi.hase.soprafs23.websocketDto.votingDto.VotingEndDTO;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.beans.factory.annotation.Qualifier;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;

        import java.util.List;
        import java.util.concurrent.Executors;
        import java.util.concurrent.ScheduledExecutorService;
        import java.util.concurrent.TimeUnit;
        import java.util.concurrent.atomic.AtomicInteger;

        import static ch.uzh.ifi.hase.soprafs23.constant.RoundStatus.NOT_STARTED;
        import static ch.uzh.ifi.hase.soprafs23.helper.GameHelper.*;
        import static ch.uzh.ifi.hase.soprafs23.helper.UserHelper.*;

        @Service
        @Transactional
        public class RoundService {

            private final RoundRepository roundRepository;
            private final GameRepository gameRepository;
            private final UserRepository userRepository;
            private final WebSocketService webSocketService;
            private final QuoteService quoteService;

            private final Logger logger = LoggerFactory.getLogger(RoundService.class);



            @Autowired
            public RoundService(@Qualifier("roundRepository") RoundRepository roundRepository,
                                @Qualifier("gameRepository")GameRepository gameRepository,
                                @Qualifier("userRepository") UserRepository userRepository,
                                WebSocketService webSocketService,
                                QuoteService quoteService) {
                this.roundRepository = roundRepository;
                this.gameRepository = gameRepository;
                this.userRepository = userRepository;
                this.webSocketService=webSocketService;
                this.quoteService=quoteService;
            }

            public void createAllRounds(Game game) {
                int roundCounter = 1;
                List<Character> letters = game.getRoundLetters();
                for (Character letter : letters) {
                    Round newRound = new Round();
                    newRound.setGame(game);
                    newRound.setRoundNumber(roundCounter);
                    newRound.setStatus(NOT_STARTED);
                    newRound.setLetter(letter);
                    roundRepository.save(newRound);
                    roundCounter++;
                }
                roundRepository.flush();
            }


            //stopRound
            public void stopRound(int gamePin, String userToken, int roundNumber) {

                Game game = gameRepository.findByGamePin(gamePin);
                checkIfGameExists(game);
                checkIfGameIsRunning(game);

                User user = userRepository.findByToken(userToken);
                checkIfUserExists(user);
                checkIfUserIsInGame(game, user);


                Round round = roundRepository.findByGameAndRoundNumber(game, roundNumber);
                RoundHelper.checkIfRoundExists(round);
                RoundHelper.checkIfRoundIsRunning(round);
                round.setStatus(RoundStatus.FINISHED);

                roundRepository.saveAndFlush(round);
                RoundEndDTO roundEndDTO=new RoundEndDTO();
                roundEndDTO.setRound(game.getCurrentRound());
                webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin, roundEndDTO);
                voteTimeControl(gamePin);

            }

            public void nextRound(int gamePin) {

                Game game = gameRepository.findByGamePin(gamePin);
                int currentRound= game.incrementRound();
                Round round = roundRepository.findByGameAndRoundNumber(game,currentRound);

                round.setStatus(RoundStatus.RUNNING);
                roundRepository.save(round);
                gameRepository.saveAndFlush(game);

                LetterDTO letterDTO = new LetterDTO();
                letterDTO.setLetter(round.getLetter());
                letterDTO.setRound(round.getRoundNumber());
                webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION +gamePin, letterDTO);
            }


            public void startRoundTime(int gamePin) {
                logger.info("starting");
                Game game = gameRepository.findByGamePin(gamePin);
                Round round = roundRepository.findByGameAndRoundNumber(game, game.getCurrentRound());
                int roundLength = game.getRoundLength().getDuration();

                String logInfo = String.format("roundLength: %d.", roundLength);
                logger.info(logInfo);

                AtomicInteger remainingTime = new AtomicInteger(roundLength);

                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                Runnable roundTimerTask = new Runnable() {
                    @Override
                    public void run() {
                        int timeLeft = remainingTime.addAndGet(-1);

                        if (isRoundFinished(gamePin)) {
                            String logInfo = String.format("Time stopped so standard timer stopped in game: %d.", gamePin);
                            logger.info(logInfo);
                            executor.shutdown(); // Stop the executor
                        } else if (noMoreTimeRemaining(timeLeft)) {
                            // Finish round
                            finishRoundNoTimeLeft(timeLeft, round, gamePin,game.getCurrentRound(), executor);
                            executor.shutdown();
                        } else {
                            timeLeftUpdate(timeLeft, round, gamePin);
                        }
                    }
                };

                executor.scheduleAtFixedRate(roundTimerTask, 5000, 1000, TimeUnit.MILLISECONDS);
            }

            public void skipRequest(int gamePin, String userToken){

                User user = userRepository.findByToken(userToken);
                Game game = gameRepository.findByGamePin(gamePin);

                checkIfUserExists(user);
                checkIfUserIsInGame(game, user);

                SkipManager skipManager = SkipRepository.findByGameId(gamePin);
                skipManager.userWantsToSkip(user);
            }
            public void timeLeftUpdate(int timeLeft, Round round, int gamePin) {
                String logInfo = String.format("timeLeft: %d, current round status: %s", timeLeft, round.getStatus());
                logger.info(logInfo);

                RoundTimerDTO roundTimerDTO = new RoundTimerDTO();
                roundTimerDTO.setTimeRemaining(timeLeft);
                webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin, roundTimerDTO);
            }

            void finishRoundNoTimeLeft(int timeLeft, Round round, int gamePin,int currentRound, ScheduledExecutorService executor) {
                round.setStatus(RoundStatus.FINISHED);
                roundRepository.saveAndFlush(round);
                String logInfo = String.format("timeLeft: %d.", timeLeft);
                logger.info(logInfo);
                RoundEndDTO roundEndDTO = new RoundEndDTO();
                roundEndDTO.setRound(currentRound);
                webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin, roundEndDTO);
                executor.shutdown(); // Stop the executor
                voteTimeControl(gamePin);
            }

            void voteTimeControl(int gamePin){

                String logInfo = String.format("Voting starting for game: %d.", gamePin);
                logger.info(logInfo);

                gameRepository.findByGamePin(gamePin);
                int currentVotingRound = 1;
                votingTimer(gamePin,currentVotingRound);

            }


            void votingScoreOverviewTimer(int gamePin, int currentVotingRound) {
                logger.info("started");

                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                Game game = gameRepository.findByGamePin(gamePin);
                AtomicInteger remainingTime = new AtomicInteger(15);

                Runnable resultTimerTask = new Runnable() {
                    boolean isQuoteSent = false;
                    @Override
                    public void run() {
                        int timeLeft = remainingTime.addAndGet(-1);
                        SkipManager skipManager = SkipRepository.findByGameId(gamePin);
                        if (!isQuoteSent) {
                            scheduleSendFact(gamePin);
                            isQuoteSent = true;
                        }

                        if (noMoreTimeRemaining(timeLeft) || skipManager.allPlayersWantToContinue()) {
                            executor.shutdown(); // Stop the executor
                            cleanUpSkipForNextRound(gamePin);

                            if (isLastCategory(gamePin, currentVotingRound)) {
                                goToScoreBoardOrWinnerPage(gamePin, game.getCurrentRound());
                            } else {
                                ResultNextVoteDTO resultNextVote = new ResultNextVoteDTO();
                                int currentVotingRoundIncremented = currentVotingRound + 1;

                                resultNextVote.setRound(game.getCurrentRound());
                                resultNextVote.setCategoryIndex(currentVotingRound);
                                webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin, resultNextVote);

                                votingTimer(gamePin, currentVotingRoundIncremented);
                            }
                        } else {
                            updateResultOverviewTimer(timeLeft, gamePin);
                        }
                    }
                };

                // Schedule resultTimerTask to run every 1 second after an initial delay of 750 milliseconds
                executor.scheduleAtFixedRate(resultTimerTask, 750, 1000, TimeUnit.MILLISECONDS);
            }

            private void updateResultOverviewTimer(int timeLeft, int gamePin) {
                ResultTimerDTO resultTimerDTO= new ResultTimerDTO();
                resultTimerDTO.setTimeRemaining(timeLeft);
                webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin,resultTimerDTO);
            }

            void goToScoreBoardOrWinnerPage(int gamePin, int currentRound) {
                if (isFinalRound(gamePin)){
                    WebSocketDTO resultWinnerDTO = WebSocketDTOCreator.resultWinner();
                    webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin,resultWinnerDTO);
                    endGame(gamePin);
                }
                else{
                    ResultScoreBoardDTO resultScoreBoardDTO = new ResultScoreBoardDTO();
                    resultScoreBoardDTO.setRound(currentRound);
                    webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin,resultScoreBoardDTO);
                    scheduleNextRound(gamePin);
                }
            }

            static boolean noMoreTimeRemaining(int timeRemaining){
                return timeRemaining<=0;
            }

            void votingTimer(int gamePin, int currentVotingRound) {
                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                Game game = gameRepository.findByGamePin(gamePin);
                Runnable votingTimerTask = new Runnable() {
                    int timeRemaining = 30;
                    // Time remaining in seconds

                    @Override
                    public void run() {
                        timeRemaining -= 1;
                        SkipManager skipManager = SkipRepository.findByGameId(gamePin);


                        if (noMoreTimeRemaining(timeRemaining) || skipManager.allPlayersWantToContinue()) {
                            cleanUpSkipForNextRound(gamePin);
                            executor.shutdown(); // Stop the executor

                            VotingEndDTO votingEndDTO = new VotingEndDTO();
                            votingEndDTO.setCategoryIndex(currentVotingRound);
                            votingEndDTO.setRound(game.getCurrentRound());

                            webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin, votingEndDTO);
                            logger.info("Voting ended, the users see voting results now.");
                            votingScoreOverviewTimer(gamePin, currentVotingRound);
                        } else {
                            remaingingVotingTimeUpdate();
                        }
                    }

                    void remaingingVotingTimeUpdate() {
                        VotingTimerDTO votingTimerDTO = new VotingTimerDTO();
                        votingTimerDTO.setTimeRemaining(timeRemaining);
                        webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin, votingTimerDTO);
                        String logInfo = String.format("Time remaining for voting: %d", timeRemaining);
                        logger.info(logInfo);
                    }
                };

                executor.scheduleAtFixedRate(votingTimerTask, 2000, 1000, TimeUnit.MILLISECONDS);
            }


            void scheduleNextRound(int gamePin) {
                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                AtomicInteger timeRemaining = new AtomicInteger(11);


                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        int remainingTime = timeRemaining.getAndDecrement();

                        if (isGameFinished(gamePin)) {
                            //because to few players remaining
                            executor.shutdown();
                            logger.info("Game closed: " + gamePin);
                        }
                        else if (noMoreTimeRemaining(remainingTime)) {
                            executor.shutdown(); // Stop the executor
                            nextRound(gamePin);
                            startRoundTime(gamePin);
                        } else {
                            ScoreboardTimerDTO scoreboardTimerDTO = new ScoreboardTimerDTO();
                            scoreboardTimerDTO.setTimeRemaining(remainingTime);
                            webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin, scoreboardTimerDTO);
                        }
                    }
                };

                // Schedule the task to run after the specified delay, and repeat every 1 second
                executor.scheduleAtFixedRate(task, 700, 1000, TimeUnit.MILLISECONDS);
            }
            public void scheduleSendFact(int gamePin) {
                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                executor.schedule(() -> sendFact(gamePin), 0, TimeUnit.MILLISECONDS);
                executor.shutdown();
            }


            private void sendFact(int gamePin) {
                FactHolder factHolder = quoteService.generateFact();
                FactDTO factDTO = new FactDTO();
                factDTO.setFact(factHolder.getFact());
                webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin,factDTO);
            }


            boolean isFinalRound(int gamePin) {
                Game game= gameRepository.findByGamePin(gamePin);
                return game.getRounds()==game.getCurrentRound();
            }
            boolean isLastCategory(int gamePin, int currentVotingRound) {
                Game game=gameRepository.findByGamePin(gamePin);
                return currentVotingRound==game.getNumberOfCategories();
            }
            boolean isGameFinished(int gamePin) {
                //because all users left the game
                Game game = gameRepository.findByGamePin(gamePin);
                return game.getStatus()==GameStatus.CLOSED;
            }
            boolean isRoundFinished(int gamePin){
                Game game = gameRepository.findByGamePin(gamePin);
                Round round = roundRepository.findByGameAndRoundNumber(game, game.getCurrentRound());
                return round.getStatus()==RoundStatus.FINISHED;
            }

            void endGame(int gamePin){
                Game game=gameRepository.findByGamePin(gamePin);
                game.setStatus(GameStatus.CLOSED);
                gameRepository.saveAndFlush(game);
                SkipRepository.removeSkipManager(gamePin);
            }



             static void cleanUpSkipForNextRound(int gamePin) {
                SkipManager skipManager = SkipRepository.findByGameId(gamePin);
                skipManager.cleanUp();
            }

        }
