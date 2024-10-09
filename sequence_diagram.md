# Sequence Diagram

Here's the giant link just in case:
[https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5T9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxPN4-IFoOxyTAADIQaJJAJpDJZbt5IvFEvVOpNVoGdQJNBDgbl0e7L4vN4fRftkGFfMlsOtxvp8Tyfks74AucBaymqKDlAg+48rCe4Hqi6KxNiCaGC6YZumSFIGrSr6jCaRLhhaHIwNyvIGoKwowKK4qutKSYXnB5R0dojrOgSeEsuUnrekGsKxsG2hkUybqUeU0YwGJ8ZyjhybQSWqE8tmuaYABIKwSUVwviOozjK2XzTrOzYTn0BzFrphTZD2MD9oOvSGcBxlWTWQYWfOVlLpwq6+P4AReCg6C7vuvjMEe6SZJgDnnkU1BXtIACiO6pfUqXNC096qI+3TTtASAAF7xIk5TmY26C-myOmVd51VoNpql-klTryjAiH2FFAaNXO2I8YmfHkfhMDkmAYl9XWTWSWaEaFJa1E8jGQb0WEVVzqGo2sXp4LyWt3HYRq-GkkYKDcJkU2begc0UZGVGRMMEA0AdcZHUpnb1RFPW+ppCB5q1na2VcNmXnZXY5GAfYDkO-krpgXhBYEkK2ju0IwAA4qOrIxSe8VnswelXpjmU5fYo6FUGxVlQQFUwDdzW2XVrUNTNW06Wy2EIdC2OjKo00zrNQ04SNUkCeNFLXf1t3beL5oPeUNGre98hChtMvNSxCt7Z1CkfXBJ07WdyCxHzaiwnd0mK+mFioK9wDKljo4OtrEa6-BlTm4yjQi19rO7rzOP-YDQIdmx7WAX0FP8yZFQuAnjRg8lEMJdDTmw65MdqHHCcuEnZgBYja7BdgPhQNg3DwLqmTO6MKSxaeUNc+D5TXg05OU8E1NQKV5VoGzQtzs+2cAHIgT+zMqWHaaM7M-RjxPbacx7gk1yg5uwnA6-m+hGJYZ9uHG+6kuTSJjNW6xi1Ucrb3iWrDGM3L83A0psmHfIftH-LJ9CZkm-ewks-e619ZI2jrigV2p13bsQ6p7bOABJaQX9p6pnKNvL0-9Rwh1hIg6Q2IV7EzTAvUcSCTJ4OTscCOacYYuRIaMMhZJSHSHhoFdcARLAXUQskGAAApCAPIIEN3xmnFuKc25VEpLeFo2cqZ+hpv3QePlnyV2AJwqAcAICISgO+J4eCTIAEZewAGYAAslDw7-gDnPUszwEBqMoJo7Ruipj6PKEYsxUEZ5iLgeUAAVgItAm9+E8l3igNE+8v5i3mgRM+fpBY+UvgrUBy1eT6wfhrdmNU3avw4nfRShtv4xNPpvPBSSFrsiVitCB61xrMOAdbVeNSDZwJ8ftOAqQUAgAbDAJAAAzGAkIIlIltKo9RTjoC9NZCEX4uhuBRPavtAAapQfpYQxmOK0ZMtAEBmCO11BwMI8g5y9KSIAz+x0inhnKH4LQWDRiwnOcAcpMk+TYDuYYJ5MBkgZFSDANAKAeEbKgCgqxM9yghKCdgtQWlCGwMAhY1OhNaFw0LgjYuyMQpQDUV2L0sBgDYEroQfuwi4qiKIW3NKGUso5WMLVVBwIIThIwqHVMbTOogG4HgS2Cy3aCS5VAdJwAeU5Jtk9F6hhHYIHyS03iMhoH8rxU8kV0DXniodk7J5vKFUwE5Xiri8gVXHzVRsCVDMnYGuAKCs44LcV4BDi1bxEcQalkRZ2GhGc6GsMwEAA]




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
