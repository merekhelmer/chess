# Sequence Diagram

```mermaid
sequenceDiagram
    %% Participants
    actor Client
    participant Server
    participant Handler
    participant Service
    participant DataAccess
    participant db

    %% Registration
    rect rgba(0,0,128,0.1) # Registration
    Client ->> Server: [POST] /user\n{"username":" ", "password":" ", "email":" "}
    Server ->> Handler: {"username":" ", "password":" ", "email":" "}
    Handler ->> Service: register(RegisterRequest)
    Service ->> DataAccess: getUser(username)
    DataAccess ->> db: Find UserData by username
    DataAccess -->> Service: null
    Service ->> DataAccess: createUser(UserData)
    DataAccess ->> db: Add UserData
    Service ->> DataAccess: createAuth(AuthData)
    DataAccess ->> db: Add AuthData
    Service -->> Handler: RegisterResult
    Handler -->> Server: {"username": " ", "authToken": " "}
    Server -->> Client: 200\n{"username": " ", "authToken": " "}
    end

    %% Login
    rect rgba(255,165,0,0.1) # Login
    Client ->> Server: [POST] /session\n{"username":" ", "password":" "}
    Server ->> Handler: {"username":" ", "password":" "}
    Handler ->> Service: login(LoginRequest)
    Service ->> DataAccess: getUser(username)
    DataAccess ->> db: Find UserData by username
    DataAccess -->> Service: UserData
    Service ->> DataAccess: createAuth(AuthData)
    DataAccess ->> db: Add AuthData
    Service -->> Handler: LoginResult
    Handler -->> Server: {"username": " ", "authToken": " "}
    Server -->> Client: 200\n{"username": " ", "authToken": " "}
    end

    %% Logout
    rect rgba(0,128,0,0.1) # Logout
    Client ->> Server: [DELETE] /session\nauthorization: authToken
    Server ->> Handler: authToken
    Handler ->> Service: logout(authToken)
    Service ->> DataAccess: getAuth(authToken)
    DataAccess ->> db: Find AuthData by authToken
    DataAccess -->> Service: AuthData
    Service ->> DataAccess: deleteAuth(authToken)
    DataAccess ->> db: Remove AuthData
    Service -->> Handler: LogoutResult
    Handler -->> Server: {}
    Server -->> Client: 200\n{}
    end

    %% List Games
    rect rgba(255,0,0,0.1) # List Games
    Client ->> Server: [GET] /game\nauthorization: authToken
    Server ->> Handler: authToken
    Handler ->> Service: listGames(authToken)
    Service ->> DataAccess: getAuth(authToken)
    DataAccess ->> db: Find AuthData by authToken
    DataAccess -->> Service: AuthData
    Service ->> DataAccess: listGames()
    DataAccess ->> db: Retrieve all GameData
    DataAccess -->> Service: [GameData]
    Service -->> Handler: ListGamesResult
    Handler -->> Server: {"games": [...]}
    Server -->> Client: 200\n{"games": [...]}
    end

    %% Create Game
    rect rgba(128,0,128,0.1) # Create Game
    Client ->> Server: [POST] /game\nauthorization: authToken\n{"gameName":" "}
    Server ->> Handler: authToken, {"gameName":" "}
    Handler ->> Service: createGame(CreateGameRequest)
    Service ->> DataAccess: getAuth(authToken)
    DataAccess ->> db: Find AuthData by authToken
    DataAccess -->> Service: AuthData
    Service ->> DataAccess: createGame(GameData)
    DataAccess ->> db: Add GameData
    DataAccess -->> Service: gameID
    Service -->> Handler: CreateGameResult(gameID)
    Handler -->> Server: {"gameID": gameID}
    Server -->> Client: 200\n{"gameID": gameID}
    end

    %% Join Game
    rect rgba(255,255,0,0.1) # Join Game
    Client ->> Server: [PUT] /game\nauthorization: authToken\n{"playerColor":" ", "gameID": 1234}
    Server ->> Handler: authToken, {"playerColor":" ", "gameID": 1234}
    Handler ->> Service: joinGame(JoinGameRequest)
    Service ->> DataAccess: getAuth(authToken)
    DataAccess ->> db: Find AuthData by authToken
    DataAccess -->> Service: AuthData
    Service ->> DataAccess: getGame(gameID)
    DataAccess ->> db: Find GameData by gameID
    DataAccess -->> Service: GameData
    Service ->> Service: Check if requested playerColor is available
    Service ->> DataAccess: updateGame(GameData)
    DataAccess ->> db: Update GameData with new player
    Service -->> Handler: JoinGameResult
    Handler -->> Server: {}
    Server -->> Client: 200\n{}
    end

    %% Clear Application
    rect rgba(128,128,128,0.1) # Clear Application
    Client ->> Server: [DELETE] /db
    Server ->> Handler: request
    Handler ->> Service: clear()
    Service ->> DataAccess: clearAuthData()
    DataAccess ->> db: Remove all AuthData
    Service ->> DataAccess: clearGameData()
    DataAccess ->> db: Remove all GameData
    Service ->> DataAccess: clearUserData()
    DataAccess ->> db: Remove all UserData
    Service -->> Handler: ClearResult
    Handler -->> Server: {}
    Server -->> Client: 200\n{}
    end
