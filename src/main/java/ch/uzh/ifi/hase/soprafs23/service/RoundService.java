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
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.beans.factory.annotation.Qualifier;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;

        import java.util.List;
        import java.util.Timer;
        import java.util.TimerTask;
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


            public void startRoundTime(int gamePin){
                logger.info("starting");
                Game game = gameRepository.findByGamePin(gamePin);
                Round round = roundRepository.findByGameAndRoundNumber(game, game.getCurrentRound());
                int roundLength = game.getRoundLength().getDuration();

                String logInfo = String.format("roundLength: %d.", roundLength);
                logger.info(logInfo);

                AtomicInteger remainingTime = new AtomicInteger(roundLength);

                Timer roundTimer = new Timer();
                TimerTask roundTimerTask = new TimerTask() {

                    @Override
                    public void run() {
                        int timeLeft = remainingTime.addAndGet(-1);

                        if (isRoundFinished(gamePin)){
                            String logInfo = String.format("Time stopped so standard timer stopped in game: %d.", gamePin);
                            logger.info(logInfo);
                            roundTimer.cancel();

                        }
                        else if (noMoreTimeRemaining(timeLeft)) {
                            //finish round
                            finishRoundNoTimeLeft(timeLeft, round, gamePin, roundTimer);

                        }

                        else {
                            timeLeftUpdate(timeLeft, round, gamePin);
                        }
                    }
                };

                roundTimer.scheduleAtFixedRate(roundTimerTask,5000, 1000); // Schedule the task to run every 1 seconds (1000 ms)
            }

            void timeLeftUpdate(int timeLeft, Round round, int gamePin) {
                String logInfo = String.format("timeLeft: %d, current round status: %s", timeLeft, round.getStatus());
                logger.info(logInfo);

                RoundTimerDTO roundTimerDTO = new RoundTimerDTO();
                roundTimerDTO.setTimeRemaining(timeLeft);
                webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin, roundTimerDTO);
            }

            void finishRoundNoTimeLeft(int timeLeft, Round round, int gamePin, Timer roundTimer) {
                round.setStatus(RoundStatus.FINISHED);
                roundRepository.saveAndFlush(round);
                String logInfo = String.format("timeLeft: %d.", timeLeft);
                logger.info(logInfo);
                String fill="roundEnd";
                RoundEndDTO roundEndDTO = new RoundEndDTO();
                roundEndDTO.setRounded(fill);
                webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin, roundEndDTO);
                roundTimer.cancel();
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

                Timer resultTimer = new Timer();

                AtomicInteger remainingTime = new AtomicInteger(15);
                TimerTask resultTimerTask = new TimerTask() {

                    @Override
                    public void run() {
                        int timeLeft = remainingTime.addAndGet(-1);
                        SkipManager skipManager=SkipRepository.findByGameId(gamePin);
                        if (noMoreTimeRemaining(timeLeft)  ||skipManager.allPlayersWantToContinue()) {
                            resultTimer.cancel();
                            cleanUpSkipForNextRound(gamePin);

                            if (isLastCategory(gamePin,currentVotingRound)){

                                goToScoreBoardorWinnerPage(gamePin);
                            }

                            else {
                                WebSocketDTO webSocketDTO = WebSocketDTOCreator.resultNextVote();
                                webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin,webSocketDTO);
                                int currentVotingRoundIncremented = currentVotingRound+1;
                                votingTimer(gamePin,currentVotingRoundIncremented);

                            }
                        }
                        else {
                            ResultTimerDTO resultTimerDTO= new ResultTimerDTO();
                            resultTimerDTO.setTimeRemaining(timeLeft);
                            webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin,resultTimerDTO);
                        }
                    }
                };

                // Schedule votingTimerTask to run every 5 seconds
                resultTimer.schedule(resultTimerTask, 1500,1000);
            }

            void goToScoreBoardorWinnerPage(int gamePin) {
                if (isFinalRound(gamePin)){
                    WebSocketDTO webSocketDTO = WebSocketDTOCreator.resultWinner();
                    webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin,webSocketDTO);
                    endGame(gamePin);
                }
                else{
                    WebSocketDTO webSocketDTO = WebSocketDTOCreator.resultScoreBoard();
                    webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin,webSocketDTO);
                    scheduleNextRound(gamePin);
                }
            }

            static boolean noMoreTimeRemaining(int timeRemaining){
                return timeRemaining<=0;
            }

            void votingTimer(int gamePin, int currentVotingRound) {
                Timer votingTimer = new Timer();
                TimerTask votingTimerTask = new TimerTask() {
                    int timeRemaining = 30;
                    // Time remaining in seconds

                    @Override
                    public void run() {
                        timeRemaining -= 1;
                        SkipManager skipManager=SkipRepository.findByGameId(gamePin);

                        if (noMoreTimeRemaining(timeRemaining) || skipManager.allPlayersWantToContinue() ) {
                            cleanUpSkipForNextRound(gamePin);
                            votingTimer.cancel();

                            WebSocketDTO webSocketDTO = WebSocketDTOCreator.votingEnd();
                            webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin,webSocketDTO);
                            logger.info("Voting ended, the users see voting results now.");
                            votingScoreOverviewTimer(gamePin,currentVotingRound);
                        }
                        else {
                            remaingingVotingTimeUpdate();
                        }

                    }

                     void remaingingVotingTimeUpdate() {
                        VotingTimerDTO votingTimerDTO=new VotingTimerDTO();
                        votingTimerDTO.setTimeRemaining(timeRemaining);
                        webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION + gamePin,votingTimerDTO);
                        String logInfo = String.format("Time remaining for voting: %d", timeRemaining);
                        logger.info(logInfo);
                    }
                };
                votingTimer.schedule(votingTimerTask, 2000, 1000);
            }
            void scheduleNextRound(int gamePin) {
                Timer timer = new Timer();

                TimerTask task = new TimerTask() {
                    int timeRemaining = 11;
                    boolean factSent = false;
                    @Override
                    public void run() {
                        timeRemaining-=1;

                        if (!factSent) {
                            sendFact(gamePin);
                            factSent = true;
                        }

                        if (noMoreTimeRemaining(timeRemaining)){
                            timer.cancel();
                            nextRound(gamePin);
                            startRoundTime(gamePin);}
                        else {

                            ScoreboardTimerDTO scoreboardTimerDTO = new ScoreboardTimerDTO();
                            scoreboardTimerDTO.setTimeRemaining(timeRemaining);
                            webSocketService.sendMessageToClients(Constant.DEFAULT_DESTINATION+gamePin,scoreboardTimerDTO);
                        }
                    }
                };

                // Schedule the task to run after the specified delay
                timer.schedule(task, 1500,1000);
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
            boolean isRoundFinished(int gamePin){
                Game game = gameRepository.findByGamePin(gamePin);
                Round round = roundRepository.findByGameAndRoundNumber(game, game.getCurrentRound());
                return round.getStatus()==RoundStatus.FINISHED;
            }

            void endGame(int gamePin){
                Game game=gameRepository.findByGamePin(gamePin);
                game.setStatus(GameStatus.CLOSED);
                gameRepository.saveAndFlush(game);
            }

            public void skipRequest(int gamePin, String userToken){

                User user = userRepository.findByToken(userToken);
                Game game = gameRepository.findByGamePin(gamePin);

                checkIfUserExists(user);
                checkIfUserIsInGame(game, user);

                SkipManager skipManager = SkipRepository.findByGameId(gamePin);
                skipManager.userWantsToSkip(user);
            }

            private static void cleanUpSkipForNextRound(int gamePin) {
                SkipManager skipManager = SkipRepository.findByGameId(gamePin);
                skipManager.cleanUp();
            }

        }
