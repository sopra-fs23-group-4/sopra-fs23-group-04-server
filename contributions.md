03.04.2023

| Person                | Last Week               | Next Week          | Description                                                                                           |
|-----------------------|-------------------------|--------------------|-------------------------------------------------------------------------------------------------------|
| Alexandre Bacmann     | #77, #78, #80, #81, #82 | #107, #112         | add default picture, user entity, userPostDTO, usercontroller(login, registration) userservice for DB |
| Valentin Meyer        | #89, #90, #91, #92      | #101, #102         | enable profile editing for users and tests for the respective methods                                 |
| Christopher Narayanan | #76, #79, #93, #95      | #103, #108         | adding basic file structure    creating mpapign for user registration and login, implementing external api to allow quote creation                                                                                                  |
| Lennart Töllke        | #4, #7, #9              | #10, #13, #15, #16 | created a common layout template, registration and edit page                                          |
| Remo Wiget            | #5, #6, #8              | #11, #12, #14      | common layout, pages: Login, Dashboard                                                                |

18.04.2023

| Person                | Last Week            | Next Week     | Description                                                   |
|-----------------------|----------------------|---------------|---------------------------------------------------------------|
| Alexandre Bacmann     | #107, #112           | #123, #124    | create lettergenerator, setting endpoint, categories endpoint |
| Valentin Meyer        | #101, #102           | #114, #118    | added game creation and joining functionality                 |
| Christopher Narayanan | #103, (#108),#275           | #119, #120,     | struggelingwebsockets , adding functionality to get standard categories                                              |
| Lennart Töllke        | (#10, #13, #15, #16) | #17, #18      | task management                                               |
| Remo Wiget            | #11, #12, #14        | #17, #20, #22 | RestApi, pages: Game, Quote                                   |

25.04.2023

| Person                | Last Week       | Next Week   | Description                                                                  |
|-----------------------|-----------------|-------------|------------------------------------------------------------------------------|
| Alexandre Bacmann     | #123, #124      |#179,#180,#181| websocket scorebaord, last round, quote for winner                           |
| Valentin Meyer        | #114, #118      | #161, #162  | added the answer and voting functionality                                    |
| Christopher Narayanan | (#119) , (#120)      |  #120, #119           | struggeling with websocket                                                                    |
| Lennart Töllke        | #42, (#17, #18) | #17, #18    | struggle with websockets/sse for lobby, started with Settings and Lobby page |
| Remo Wiget            | #17, #20, #23   | #22, #24    | storageManager, pages: Categories, Board, Answer                             |

02.05.2023

| Person                | Last Week     | Next Week           | Description                                            |
|-----------------------|---------------|---------------------|--------------------------------------------------------|
| Alexandre Bacmann     |#179,#180,#181 | #33,#37,#38,#39,#40 | backend Leaderboard, testing                          |
| Valentin Meyer        | #161, #162    | #158, #159          | improved voting and enabled starting and ending rounds |
| Christopher Narayanan |# 120, #145, #108, #119              | #123, #201                 |websocket working on a page, testing repositories                                           |
| Lennart Töllke        | #17, #18      | #27, #28            | lobby page, settings page, game creation               |
| Remo Wiget            | #22, #24      | #26                 | pages: Letter, Voting                                  |

09.05.2023

| Person                | Last Week           | Next Week                  | Description                                                                       |
|-----------------------|---------------------|----------------------------|-----------------------------------------------------------------------------------|
| Alexandre Bacmann     | #33,#37,#38,#39,#40,#174|#46,#48,#200,#242,#243 | Rules page, Front leaderboard, testing                                          |
| Valentin Meyer        | #158, #159          | #160, #163                 | create utility classes and handled exceptional calls, plus other trouble shooting |
| Christopher Narayanan | #123  #201, #66, #120, #119, #277         |  #208, #197, #223, #276          | implementing fact api, websocket working locally, timer implementedn              |
| Lennart Töllke        | #27, #28            | #16, #56, #199             | Score page, Winner page, improved lobby and settings page                         |
| Remo Wiget            | #26, #5             | #198, #50                  | VotingResults page, add custom Category, redesign Dashboard, ws, fix bug Answer   |

16.05.2023

| Person                | Last Week           | Next Week                  | Description                                                                       |
|-----------------------|---------------------|----------------------------|-----------------------------------------------------------------------------------|
| Alexandre Bacmann     | #46,#48,#200,#242,#243|#183,#184,#185,#186,#199  | testing, Advanced statistics (service, controller and DTO class).                 |
| Valentin Meyer        | #160, #163          | #212, #217                 | added integration tests for GameService and AnswerService                         |
| Christopher Narayanan | #208, #197, #223, #276, (#240), #278, #80   | #244, #211,        | adding functionality that if users want they can skip round, changing how facts are sent,  adding animation for winnerquote, auto starting next round     |
| Lennart Töllke        | #16, #56, #43, #199 | #68, #63, #57, #58, #59    | added User page with advanced stats, managed uncontrolled leaving behaviour and single player  |
| Remo Wiget            | #198, #50, #51, #65 | #19, #219, #234            | VotingResult optimized for mobile, Voting loader, timers, manual continue, beautify error msg, ui |

23.05.2023

| Person                | Last Week                          | Description                                                                                                                                                                          |
|-----------------------|------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Alexandre Bacmann     | #183,#184,#185,#186,#199           |                                                                                                                                                                                      |
| Valentin Meyer        | #212, #217, #252, #262, #266, #269 | added random categories in backend, fixed sonarcloud issues, refactored CategoryService, added an integration test for AdvancedStatisticService, added more tests for helper classes |
| Christopher Narayanan |  #80, #81, #189, 190, #213, #219, #234, #244, #254, #259, #263, #265, #271, #272, #274, #275, #280, #281, #282                    | countdown page works regardless of fixing many bugs, and constraints to the number of players/categorylength, etc.,  informing frontend when too few players are in the game and if a player leaves during the game,   doing testing for quote and timecontroller,  refactor roundservice for performance,  changign dtos so that frontend recieves more information, performance related fixes ...   many small issues                                                                                                             |
| Lennart Töllke        | #68, #63, #57, #58, #59, #76            | added pop up to leave button and progress bar on score page, InGameRouter, routing to User Page, fixed Bugs, made leaderboard fancy                                                  |
| Remo Wiget            | #19, #217, #219, #234              | added random categories, countdown page, refactor quote and remove context, limit length categories & username, change UI to small letters, fixed bugs                               |

